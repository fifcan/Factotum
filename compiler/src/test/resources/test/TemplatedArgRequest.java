package test;

import java.lang.ref.WeakReference;
import java.util.List;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.concurent.ICallback;

public final class TemplatedArgRequest extends AbstractRequest {

    private final List<String> bob;

    public TemplatedArgRequest(List<String> bob){
        this.bob = bob;
    }

    @Override
    public Object getTaskHandler() {
        return new TemplatedArgument();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        return ((TemplatedArgument)handler).templatedArg(bob);
    }

    public TemplatedArgRequest setCallback( ICallback<List<String>> callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public TemplatedArgRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public TemplatedArgRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}