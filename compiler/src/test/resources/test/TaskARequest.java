package test;

import net.riotopsys.factotum.api.customize.AbstractRequest;

public final class TaskARequest extends AbstractRequest {

    private final String stuff;

    public TaskARequest(String stuff){
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
        return ((MultipuleTasks)handler).taskA(stuff);
    }

}