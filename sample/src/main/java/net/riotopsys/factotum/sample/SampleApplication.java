/*
 * Copyright 2015 C. A. Fitzgerald
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
