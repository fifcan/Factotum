package net.riotopsys.factotum.api.concurent;

import net.riotopsys.factotum.api.AbstractRequest;

import java.util.concurrent.Callable;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class RequestCallable implements Callable<ResultWrapper> {

    public final AbstractRequest request;
    private final Object handler;
    public final long serial;

    public RequestCallable( AbstractRequest request, Object handler, long serial ){
        this.request = request;
        this.handler = handler;
        this.serial = serial;
    }

    @Override
    public ResultWrapper call() throws Exception {
        if ( request.isCanceled() ){
            return null;
        }

        ICallback callback = request.getCallback();

        Object result;
        try {
            result = request.execute(handler);

            if ( callback != null ){
                callback.onSuccess(request, result);
            }

        } catch ( Exception e){
            result = e;

            if ( callback != null ){
                callback.onFailure(request, result);
            }
        }

        return new ResultWrapper(request, result);
    }

    public AbstractRequest getRequest() {
        return request;
    }
}
