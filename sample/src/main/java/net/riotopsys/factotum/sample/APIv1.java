package net.riotopsys.factotum.sample;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by afitzgerald on 2/14/15.
 */
public interface APIv1 {

    @GET("/users/{username}/library")
    public List<LibraryEntry> getLibraryForUser(@Path("username") String username );

}
