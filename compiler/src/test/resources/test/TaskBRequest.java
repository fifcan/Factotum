package test;

import net.riotopsys.factotum.api.customize.AbstractRequest;

public final class TaskBRequest extends AbstractRequest {

    private final Float stuff;

    public TaskBRequest(Float stuff){
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
        return ((MultipuleTasks)handler).taskB(stuff);
    }

}