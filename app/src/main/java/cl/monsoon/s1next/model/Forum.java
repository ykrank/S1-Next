package cl.monsoon.s1next.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Forum implements Comparable {

    @JsonProperty("fid")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("threads")
    private int threads;

    @JsonProperty("todayposts")
    private int todayPosts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getTodayPosts() {
        return todayPosts;
    }

    public void setTodayPosts(int todayPosts) {
        this.todayPosts = todayPosts;
    }

    /**
     * Sort by {@link #todayPosts}.
     */
    @Override
    public int compareTo(@NonNull Object another) {
        return getTodayPosts() - ((Forum) another).getTodayPosts();
    }
}
