package net.riotopsys.factotum.sample;

import android.app.Application;

import net.riotopsys.factotum.api.Factotum;
import net.riotopsys.factotum.api.interfaces.IOnTaskCreationCallback;

import dagger.ObjectGraph;

/**
 * Created by afitzgerald on 2/12/15.
 */
public class SampleApplication extends Application{

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        CoreModule coreModule = new CoreModule();
        objectGraph = ObjectGraph.create(coreModule);

        Factotum factotum = new Factotum.Builder()
                .setOnTaskCreationCallback(new DaggerTaskInjector(objectGraph))
                .build();

        coreModule.setFactotum(factotum);
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }
}
