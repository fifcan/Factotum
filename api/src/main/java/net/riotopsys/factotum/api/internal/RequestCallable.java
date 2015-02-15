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

package net.riotopsys.factotum.api.internal;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

import java.util.concurrent.Callable;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class RequestCallable implements Callable<ResultWrapper> {

    private final AbstractRequest request;
    private final Object handler;
    private final long serial;

    public RequestCallable(AbstractRequest request, Object handler, long serial) {
        this.request = request;
        this.handler = handler;
        this.serial = serial;
    }

    @Override
    public ResultWrapper call() throws Exception {
        if (request.isCanceled()) {
            return null;
        }

        ICallback callback = request.getCallback();

        Object result;
        try {
            result = request.execute(handler);

            if (callback != null) {
                callback.onSuccess(request, result);
            }

        } catch (Exception e) {
            result = e;

            if (callback != null) {
                callback.onFailure(request, result);
            }
        }

        return new ResultWrapper(request, result);
    }

    public AbstractRequest getRequest() {
        return request;
    }

    public long getSerial() {
        return serial;
    }
}
