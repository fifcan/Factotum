package net.riotopsys.factotum.api;

import net.riotopsys.factotum.api.customize.AbstractRequest;
import net.riotopsys.factotum.api.customize.ICancelRequest;
import net.riotopsys.factotum.api.customize.ResultCallback;
import net.riotopsys.factotum.api.customize.ITaskHandlerFactory;
import net.riotopsys.factotum.api.concurent.PauseableThreadPoolExecutor;
import net.riotopsys.factotum.api.concurent.RequestCallable;

import java.util.Iterator;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class Factotum {

    private static final String TAG = Factotum.class.getSimpleName();

    private final ITaskHandlerFactory taskHandlerFactory = new ITaskHandlerFactory() {
        @Override
        public Object getHandler(Class clazz) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private PauseableThreadPoolExecutor executor = new PauseableThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() *2,
            1, TimeUnit.MINUTES,
            new PriorityBlockingQueue<Runnable>(),
            new ThreadFactory() {
                public int count = 0;

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName(String.format("%s-%d", TAG, count++));
                    return t;
                }
            });

    private ExecutorCompletionService completionService = new ExecutorCompletionService( executor );

    private Thread resultCollector;

    private ResultCallback resultCallback = new ResultCallback(completionService) {
        @Override
        public void onResult(Object result) {

        }
    };

    public Factotum(){
        resultCollector = new Thread( resultCallback );
        resultCollector.setName(String.format("%s-collector",TAG));
        resultCollector.start();
    }

    public void shutdown(){
        resultCallback.stop();
        executor.shutdownNow();
    }

    public void addRequest( AbstractRequest request ){
        if ( request.isCanceled() ){
            return;
        }
        Object handler = taskHandlerFactory.getHandler(request.HandleingClass());
        completionService.submit(new RequestCallable(request, handler));
    }

    public synchronized void issueCancel( ICancelRequest cancelRequest ){
        executor.pause();

        Iterator<Runnable> iter = executor.getQueue().iterator();
        while ( iter.hasNext() ){
            RequestCallable callable = (RequestCallable) iter.next();
            AbstractRequest request = callable.getRequest();

            if ( cancelRequest.match(request.getGroup()) ){
                request.cancel();
            }

            if ( request.isCanceled() ){
                iter.remove();
            }
        }

        executor.resume();
    }

}
