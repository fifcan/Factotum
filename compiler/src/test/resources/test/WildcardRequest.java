package test;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Set;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class WildcardRequest extends AbstractRequest {

    private final String stuff;

    public WildcardRequest(String stuff){
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new WildCardReturn();
    }

    @Override
    public Object execute(Object handler) throws Exception{
        if ( isCanceled() ){
            return null;
        }
        return ((WildCardReturn)handler).wildcard(stuff);
    }

    public WildcardRequest setCallback(ICallback<Set<? extends Comparator<String>>> callback){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public WildcardRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public WildcardRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}