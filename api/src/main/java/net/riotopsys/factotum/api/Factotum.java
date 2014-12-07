package net.riotopsys.factotum.api;

import net.riotopsys.factotum.api.customize.ICancelRequest;
import net.riotopsys.factotum.api.concurent.PauseableThreadPoolExecutor;
import net.riotopsys.factotum.api.concurent.RequestCallable;
import net.riotopsys.factotum.api.customize.IOnTaskCompletionCallback;
import net.riotopsys.factotum.api.customize.IOnTaskCreationCallback;

import java.util.Iterator;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class Factotum {

    private static final String TAG = Factotum.class.getSimpleName();

    private PauseableThreadPoolExecutor executor;
    private ExecutorCompletionService completionService;
    private Thread resultCollector;

    private IOnTaskCompletionCallback onTaskCompletionCallback;

    private IOnTaskCreationCallback onTaskCreationCallback;

    private static AtomicLong seq = new AtomicLong(0);

    private ResultCallback resultCallback = new ResultCallback(completionService) {
        @Override
        public void onResult(Object result) {
            onTaskCompletionCallback.OnTaskCompletion(result);
        }
    };

    private Factotum( Builder builder ){

        executor = new PauseableThreadPoolExecutor(
                builder.maximumPoolSize,
                builder.maximumPoolSize,
                builder.keepAliveTime,
                builder.keepAliveTimeUnit,
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

        completionService = new ExecutorCompletionService( executor );

        onTaskCompletionCallback = builder.onTaskCompletionCallback;

        onTaskCreationCallback = builder.onTaskCreationCallback;

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
        Object handler = request.getTask();
        onTaskCreationCallback.onTaskCreation(handler);
        completionService.submit(new RequestCallable(request, handler, seq.getAndIncrement()));
    }

    public synchronized void issueCancelation( ICancelRequest cancelRequest ){
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

    public static class Builder {

        private int maximumPoolSize = Runtime.getRuntime().availableProcessors() *2;

        private long keepAliveTime = 1;

        private TimeUnit keepAliveTimeUnit = TimeUnit.MINUTES;

        private IOnTaskCompletionCallback onTaskCompletionCallback = new IOnTaskCompletionCallback() {
            @Override
            public void OnTaskCompletion(Object object) {
                //noop
            }
        };

        private IOnTaskCreationCallback onTaskCreationCallback = new IOnTaskCreationCallback() {
            @Override
            public void onTaskCreation(Object task) {
                //noop
            }
        };

        public Builder setKeepAliveTime(long keepAliveTime, TimeUnit keepAliveTimeUnit) {

            if ( maximumPoolSize <=0 ){
                throw new IllegalArgumentException("max pool size must be greater then 0");
            }
            if ( keepAliveTimeUnit == null ){
                throw new IllegalArgumentException("argument cannot be null");
            }
            this.keepAliveTime = keepAliveTime;
            this.keepAliveTimeUnit = keepAliveTimeUnit;
            return this;
        }

        public Builder setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder setOnTaskCompletionCallback(IOnTaskCompletionCallback onTaskCompletionCallback) {
            if ( onTaskCompletionCallback == null ){
                throw new IllegalArgumentException("argument cannot be null");
            }
            this.onTaskCompletionCallback = onTaskCompletionCallback;
            return this;
        }

        public Builder setOnTaskCreationCallback(IOnTaskCreationCallback onTaskCreationCallback) {
            if ( onTaskCreationCallback == null ){
                throw new IllegalArgumentException("argument cannot be null");
            }
            this.onTaskCreationCallback = onTaskCreationCallback;
            return this;
        }

        public Factotum Build(){
            return new Factotum(this);
        }
    }

}
