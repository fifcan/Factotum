package net.riotopsys.factotum.sample;

import net.riotopsys.factotum.api.Factotum;

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

}
