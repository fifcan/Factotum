package test;

import net.riotopsys.factotum.api.customize.AbstractRequest;

public final class VoidReturnRequest extends AbstractRequest {

    private final String stuff;

    public VoidReturnRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Class HandleingClass() {
        return VoidReturnTask.class;
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