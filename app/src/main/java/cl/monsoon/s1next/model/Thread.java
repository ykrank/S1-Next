package cl.monsoon.s1next.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Ambiguity in naming due to {@link java.lang.Thread}.
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Thread implements Parcelable {

    public static final Parcelable.Creator<Thread> CREATOR =
            new Parcelable.Creator<Thread>() {
                @Override
                public Thread createFromParcel(Parcel parcel) {
                    return new Thread(parcel);
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

    @JsonProperty("replies")
    private int replies;

    @JsonProperty("readperm")
    private int permission;

    public Thread() {

    }

    private Thread(Parcel parcel) {
        id = parcel.readString();
        title = parcel.readString();
        replies = parcel.readInt();
        permission = parcel.readInt();
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThreadListInfo {

        @JsonProperty("threads")
        private int threads;

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }
    }
}
