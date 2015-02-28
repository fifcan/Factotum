package test;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.ref.WeakReference;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

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

    public VoidReturnRequest setCallback(ICallback<Object> callback) {
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public VoidReturnRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public VoidReturnRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

}