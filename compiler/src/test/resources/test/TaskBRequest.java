package test;

import net.riotopsys.factotum.api.AbstractRequest;

public final class TaskBRequest extends AbstractRequest {

    private final Float stuff;

    public TaskBRequest(Float stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTask() {
        return new MultipuleTasks();
    }

    @Override
    public Object execute(Object handler) {
        if ( isCanceled() ){
            return null;
        }
        return ((MultipuleTasks)handler).taskB(stuff);
    }

}