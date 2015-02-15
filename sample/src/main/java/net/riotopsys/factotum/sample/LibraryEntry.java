package net.riotopsys.factotum.sample;

import com.google.gson.annotations.SerializedName;

/**
 * Created by afitzgerald on 2/14/15.
 */
public class LibraryEntry {

    public static class Anime {
        public long id;
        public String title;
        public String cover_image;
    }

    public static class Rating {
        public String type;
        public Float value;
    }

    public Anime anime;

    @SerializedName("episodes_watched")
    public int episodesWatched;
    public int id;
    //TODO: convert dates
    //    public Date last_watched; //": "2013-12-09T14:56:39.285Z"
    public String notes;

    @SerializedName("private")
    public boolean isPrivate;

    public Rating rating;

    @SerializedName("rewatched_times")
    public int rewatchedTimes;

    public boolean rewatching;
    public String status;
//            public Date "updated_at"; "1970-01-01T00:00:00.000Z"

}
