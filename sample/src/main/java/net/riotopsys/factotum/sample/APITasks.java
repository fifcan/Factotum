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

import net.riotopsys.factotum.api.annotation.Task;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by afitzgerald on 2/14/15.
 */
public class APITasks {

    @Inject
    APIv1 api;

    @Inject
    LruCache lruCache;

    @Task
    public List<LibraryEntry> getLibrary( String username) {

        String key = "APITasks:getLibrary:" + username;

        List<LibraryEntry> value = (List<LibraryEntry>) lruCache.get(key);

        if (value == null) {
            value = api.getLibraryForUser(username);
            lruCache.put(key, value);
        }

        return value;
    }

}
