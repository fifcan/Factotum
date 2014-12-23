package net.riotopsys.factotum.api;

import net.riotopsys.factotum.api.concurent.ICallback;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by afitzgerald on 8/27/14.
 */
public abstract class AbstractRequest {

    private AtomicBoolean canceled = new AtomicBoolean(false);
    protected Object group = null;
    protected int priority = 0;
    protected WeakReference<ICallback> callbackRef = null;

    public abstract Object execute(Object handler) throws Exception;
    public abstract Object getTaskHandler();

    public boolean isCanceled(){
        return canceled.get();
    }

    public void cancel(){
        canceled.set(true);
    }

//    public AbstractRequest setGroup(Object group){
//        this.group = group;
//        return this;
//    }
//
//    public AbstractRequest setPriority(int priority) {
//        this.priority = priority;
//        return this;
//    }

    public int getPriority() {
        return priority;
    }

    public Object getGroup() {
        return group;
    }

    public ICallback getCallback() {
        if ( callbackRef == null ){
            return null;
        }
        return callbackRef.get();
    }

//    protected AbstractRequest internalSetCallback(ICallback callback){
//        callbackRef = new WeakReference<ICallback>(callback);
//        return this;
//    }
}
