package net.riotopsys.factotum.sample;

import net.riotopsys.factotum.api.annotation.Task;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by afitzgerald on 2/14/15.
 */
public class APITasks {

    @Inject
    APIv1 api;

    @Task
    public List<LibraryEntry> getLibrary( String username){
        return api.getLibraryForUser(username);
    }

}
