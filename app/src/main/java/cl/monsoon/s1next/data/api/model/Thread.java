package cl.monsoon.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Ambiguity in naming due to {@link java.lang.Thread}.
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Thread implements Parcelable {

    public static final Parcelable.Creator<Thread> CREATOR = new Parcelable.Creator<Thread>() {

        @Override
        public Thread createFromParcel(Parcel source) {
            return new Thread(source);
        }

        @Override
        public Thread[] newArray(int i) {
            return new Thread[i];
        }
    };

    @JsonProperty("tid")
    private String id;

    @JsonProperty("subject")
    private String title;

    @JsonProperty("author")
    private String authorName;

    @JsonProperty("authorid")
    private String authorId;

    @JsonProperty("replies")
    private int replies;

    @JsonProperty("readperm")
    private int permission;

    public Thread() {}

    private Thread(Parcel source) {
        id = source.readString();
        title = source.readString();
        replies = source.readInt();
        permission = source.readInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // unescape some basic XML entities
        this.title = StringEscapeUtils.unescapeXml(title);
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeInt(replies);
        dest.writeInt(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thread thread = (Thread) o;
        return Objects.equal(replies, thread.replies) &&
                Objects.equal(permission, thread.permission) &&
                Objects.equal(id, thread.id) &&
                Objects.equal(title, thread.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, replies, permission);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ThreadListInfo {

        @JsonProperty("threads")
        private int threads;

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThreadListInfo that = (ThreadListInfo) o;
            return Objects.equal(threads, that.threads);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(threads);
        }
    }
}
