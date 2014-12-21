package test;

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

    public AbstractRequest setCallback( ICallback<MultipuleTasks> callback ){
        return internalSetCallback( callback );
    }

}