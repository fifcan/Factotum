package net.riotopsys.factotum.api.internal;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

import java.util.concurrent.Callable;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class RequestCallable implements Callable<ResultWrapper> {

    private final AbstractRequest request;
    private final Object handler;
    private final long serial;

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

    public long getSerial() {
        return serial;
    }
}
