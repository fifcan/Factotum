package test;

import net.riotopsys.factotum.api.customize.AbstractRequest;

public final class TaskCRequest extends AbstractRequest {

    private final Object stuff;

    public TaskCRequest(Object stuff){
        this.stuff = stuff;
    }

    @Override
    public Class HandleingClass() {
        return MultipuleTasks.class;
    }

    @Override
    public Object execute(Object handler) {
        if ( isCanceled() ){
            return null;
        }
        return ((MultipuleTasks)handler).taskC(stuff);
    }

}