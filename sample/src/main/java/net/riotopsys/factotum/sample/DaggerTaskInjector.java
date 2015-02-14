package net.riotopsys.factotum.sample;

import net.riotopsys.factotum.api.interfaces.IOnTaskCreationCallback;

import dagger.ObjectGraph;

/**
 * Created by afitzgerald on 2/14/15.
 */
public class DaggerTaskInjector implements IOnTaskCreationCallback {

    private final ObjectGraph objectGraph;

    public DaggerTaskInjector( ObjectGraph objectGraph ){
        this.objectGraph = objectGraph;
    }

    @Override
    public void onTaskCreation(Object task) {
        objectGraph.inject(task);
    }
}
