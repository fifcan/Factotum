package test;

import java.lang.ref.WeakReference;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.concurent.ICallback;

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

    public SpecialTaskHandlerRequest setCallback(ICallback<String> callback){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public SpecialTaskHandlerRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public SpecialTaskHandlerRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}