package test;

import java.lang.ref.WeakReference;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class TaskARequest extends AbstractRequest {

    private final String stuff;

    public TaskARequest(String stuff){
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
        return ((MultipuleTasks)handler).taskA(stuff);
    }

    public TaskARequest setCallback( ICallback<String> callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public TaskARequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public TaskARequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}