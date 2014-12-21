package test;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.concurent.ICallback;

public final class VoidReturnRequest extends AbstractRequest {

    private final String stuff;

    public VoidReturnRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new VoidReturnTask();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        ((VoidReturnTask)handler).voidReturn(stuff);
        return null;
    }

}