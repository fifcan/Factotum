package test;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.ref.WeakReference;
import java.util.List;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class TemplatedRequest extends AbstractRequest {

    private final String stuff;

    public TemplatedRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new Templated();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        return ((Templated)handler).templated(stuff);
    }

    public TemplatedRequest setCallback( ICallback<List<String>> callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public TemplatedRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public TemplatedRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}