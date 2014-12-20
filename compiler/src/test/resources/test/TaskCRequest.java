package test;

import net.riotopsys.factotum.api.AbstractRequest;

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

}