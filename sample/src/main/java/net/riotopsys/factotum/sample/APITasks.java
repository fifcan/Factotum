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
