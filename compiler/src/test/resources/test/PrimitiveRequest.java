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

package test;

import java.lang.Exception;
import java.lang.Float;
import java.lang.Object;
import java.lang.Override;
import java.lang.ref.WeakReference;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.interfaces.ICallback;

public final class PrimitiveRequest extends AbstractRequest {
    private final int stuff;

    public PrimitiveRequest(int stuff) {
        this.stuff = stuff;
    }

    @Override
    public Object getTaskHandler() {
        return new Primitive();
    }

    @Override
    public Object execute(Object handler) throws Exception {
        if (isCanceled() ) {
            return null;
        }
        return ((Primitive)handler).primitive(stuff);
    }

    public PrimitiveRequest setCallback(ICallback<Float> callback) {
        callbackRef = new WeakReference<ICallback>(callback);
        return this;
    }

    public PrimitiveRequest setGroup(Object group) {
        this.group = group;
        return this;
    }

    public PrimitiveRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }
}