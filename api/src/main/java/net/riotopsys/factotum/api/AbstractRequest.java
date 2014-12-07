package net.riotopsys.factotum.api;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by afitzgerald on 8/27/14.
 */
public abstract class AbstractRequest {

    private AtomicBoolean canceled = new AtomicBoolean(false);
    private Object group = null;
    private int priority = 0;

    public abstract Object execute(Object handler);

    public boolean isCanceled(){
        return canceled.get();
    }

    public void cancel(){
        canceled.set(true);
    }

    public AbstractRequest setGroup(Object group){
        this.group = group;
        return this;
    }

    public AbstractRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public Object getGroup() {
        return group;
    }

    public abstract Object getTask();
}
