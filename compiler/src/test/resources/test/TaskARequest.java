package test;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.concurent.ICallback;

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

    public AbstractRequest setCallback( ICallback<String> callback ){
        return internalSetCallback( callback );
    }

}