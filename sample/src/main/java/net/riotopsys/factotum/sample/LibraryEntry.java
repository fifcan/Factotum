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
