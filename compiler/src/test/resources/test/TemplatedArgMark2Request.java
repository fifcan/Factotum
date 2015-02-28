package test;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class TemplatedArgMark2Request extends AbstractRequest {

    private final List<Set<? extends Comparator<String>>> bob;

    public TemplatedArgMark2Request(List<Set<? extends Comparator<String>>> bob){
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
        return ((TemplatedArgument)handler).templatedArgMark2(bob);
    }

    public TemplatedArgMark2Request setCallback( ICallback<Set<? extends Comparator<String>>> callback ){
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public TemplatedArgMark2Request setGroup(Object group){
        this.group = group;
        return this;
    }

    public TemplatedArgMark2Request setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}