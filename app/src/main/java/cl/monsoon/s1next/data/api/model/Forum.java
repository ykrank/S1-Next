package cl.monsoon.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringEscapeUtils;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Forum implements Parcelable {

    public static final Parcelable.Creator<Forum> CREATOR = new Parcelable.Creator<Forum>() {

        @Override
        public Forum createFromParcel(Parcel source) {
            return new Forum(source);
        }

        @Override
        public Forum[] newArray(int i) {
            return new Forum[i];
        }
    };

    @JsonProperty("fid")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("threads")
    private int threads;

    @JsonProperty("todayposts")
    private int todayPosts;

    public Forum() {

    }

    private Forum(Parcel source) {
        id = source.readString();
        name = source.readString();
        threads = source.readInt();
        todayPosts = source.readInt();
    }

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
        // unescape some basic XML entities
        this.name = StringEscapeUtils.unescapeXml(name);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(threads);
        dest.writeInt(todayPosts);
    }
}
