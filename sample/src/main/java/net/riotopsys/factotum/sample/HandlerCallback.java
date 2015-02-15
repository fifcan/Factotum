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
