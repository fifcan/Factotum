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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by afitzgerald on 12/30/14.
 */
public class PriorityComparator implements Comparator<Runnable>, Serializable {
    @Override
    public int compare(Runnable o1, Runnable o2) {
        RequestCallable rhs = (RequestCallable) ((FriendlyCompletionService.QueueingFuture) o2).getOriginalCallable();
        RequestCallable lhs = (RequestCallable) ((FriendlyCompletionService.QueueingFuture) o1).getOriginalCallable();

        int result = rhs.getRequest().getPriority() - lhs.getRequest().getPriority();
        if (result != 0) {
            return result;
        }
        return (int) (lhs.getSerial() - rhs.getSerial());
    }
}
