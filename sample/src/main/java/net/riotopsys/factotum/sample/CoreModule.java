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

import android.util.LruCache;

import net.riotopsys.factotum.api.Factotum;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

/**
 * Created by afitzgerald on 2/12/15.
 */
@Module(
        injects = {
                MainActivity.class,
                APITasks.class
        }
)
public class CoreModule {

    private Factotum factotum;

    public void setFactotum(Factotum factotum) {
        this.factotum = factotum;
    }

    @Provides
    protected APIv1 provideApi( ){
        return new RestAdapter.Builder()
                .setEndpoint("https://hummingbird.me/api/v1")
                .setLogLevel((BuildConfig.DEBUG)? RestAdapter.LogLevel.FULL: RestAdapter.LogLevel.NONE )
                .build().create(APIv1.class);
    }

    @Provides
    protected Factotum provideFactotum(){
        return factotum;
    }

    @Singleton
    @Provides
    protected LruCache provideCache(){
        return new LruCache(20);
    }

}
