package net.riotopsys.factotum.sample;

import android.os.Handler;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;


/**
 * Created by afitzgerald on 12/21/14.
 */
public abstract class HandlerCallback<T> implements ICallback<T> {

    public abstract void onSuccessInHandler(AbstractRequest abstractRequest, T result);
    public abstract void onFailureInHandler(AbstractRequest abstractRequest, Object error);

    private Handler handler;

    public HandlerCallback(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onSuccess(final AbstractRequest abstractRequest, final T o) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onSuccessInHandler(abstractRequest, o);
            }
        });
    }

    @Override
    public void onFailure(final AbstractRequest abstractRequest, final Object o) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailureInHandler(abstractRequest, o);
            }
        });
    }


}
