package test;

import net.riotopsys.factotum.api.AbstractRequest;

public final class VoidReturnRequest extends AbstractRequest {

    private final String stuff;

    public VoidReturnRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTask() {
        return new VoidReturnTask();
    }

    @Override
    public Object execute(Object handler) {
        if ( isCanceled() ){
            return null;
        }
        ((VoidReturnTask)handler).voidReturn(stuff);
        return null;
    }

}