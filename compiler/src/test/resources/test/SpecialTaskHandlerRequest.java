package test;

import net.riotopsys.factotum.api.AbstractRequest;

public final class SpecialTaskHandlerRequest extends AbstractRequest {

    private final String stuff;

    public SpecialTaskHandlerRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new SimpleTask();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        return ((SimpleTask)handler).specialTaskHandler(stuff);
    }

}