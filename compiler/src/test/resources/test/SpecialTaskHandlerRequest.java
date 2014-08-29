package test;

import net.riotopsys.factotum.api.abstracts.AbstractRequest;

public final class SpecialTaskHandlerRequest extends AbstractRequest {

    private final String stuff;

    public SpecialTaskHandlerRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Class HandleingClass() {
        return SimpleTask.class;
    }

    @Override
    public Object execute(Object handler) {
        if ( isCanceled() ){
            return null;
        }
        return ((SimpleTask)handler).specialTaskHandler(stuff);
    }

}