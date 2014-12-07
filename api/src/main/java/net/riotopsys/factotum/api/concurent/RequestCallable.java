package net.riotopsys.factotum.api.concurent;

import net.riotopsys.factotum.api.AbstractRequest;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class RequestCallable implements Callable<Object>, Comparable<RequestCallable> {

    private final AbstractRequest request;
    private final Object handler;
    private final long serial;

    public RequestCallable( AbstractRequest request, Object handler, long serial ){
        this.request = request;
        this.handler = handler;
        this.serial = serial;
    }

    @Override
    public Object call() throws Exception {
        if ( request.isCanceled() ){
            return null;
        }
        return request.execute(handler);
    }

    @Override
    public int compareTo(RequestCallable other) {
        int result = request.getPriority() - other.request.getPriority();
        if ( result != 0 ){
            return result;
        }
        return (int) (serial - other.serial);
    }

    public AbstractRequest getRequest() {
        return request;
    }
}
