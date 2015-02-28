package test;

import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.ref.WeakReference;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class TaskBRequest extends AbstractRequest {

    private final Float stuff;

    public TaskBRequest(Float stuff){
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
        return ((MultipuleTasks)handler).taskB(stuff);
    }

    public TaskBRequest setCallback( ICallback<Integer> callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public TaskBRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public TaskBRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}