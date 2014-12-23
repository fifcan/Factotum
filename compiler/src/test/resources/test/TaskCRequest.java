package test;

import java.lang.ref.WeakReference;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.concurent.ICallback;

public final class TaskCRequest extends AbstractRequest {

    private final Object stuff;

    public TaskCRequest(Object stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new MultipuleTasks();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        return ((MultipuleTasks)handler).taskC(stuff);
    }

    public TaskCRequest setCallback( ICallback<MultipuleTasks> callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public TaskCRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public TaskCRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }
}