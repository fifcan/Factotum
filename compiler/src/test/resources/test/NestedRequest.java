package test;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class NestedRequest extends AbstractRequest {

    private final String stuff;

    public NestedRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new Nested();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        return ((Nested)handler).nested(stuff);
    }

    public NestedRequest setCallback( ICallback< List<Map<String, Set<Integer> > > > callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public NestedRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public NestedRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}