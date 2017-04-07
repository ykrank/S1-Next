package me.ykrank.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import org.apache.commons.lang3.StringEscapeUtils;

import me.ykrank.s1next.data.SameItem;
import me.ykrank.s1next.util.L;

/**
 * Ambiguity in naming due to {@link java.lang.Thread}.
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Thread implements Parcelable, Cloneable, SameItem {

    private static final String TAG = Thread.class.getSimpleName();
    @JsonProperty("tid")
    private String id;

    @JsonProperty("subject")
    private String title;

    /**
     * perhaps '-'
     */
    @JsonProperty("replies")
    private String replies;

    @JsonProperty("readperm")
    private int permission;

    @JsonProperty("author")
    private String author;

    @JsonProperty("authorId")
    private int authorId;

    @JsonProperty("displayorder")
    private int displayOrder;

    @JsonProperty("typeid")
    private String typeId;

    @JsonProperty("fid")
    private String fid;

    @JsonIgnore
    private String typeName;

    @JsonIgnore
    private boolean hide = false;
    /**
     * reply count when last view
     */
    @JsonIgnore
    private int lastReplyCount;

    public Thread() {
    }

    protected Thread(Parcel in) {
        id = in.readString();
        title = in.readString();
        replies = in.readString();
        permission = in.readInt();
        author = in.readString();
        authorId = in.readInt();
        displayOrder = in.readInt();
        typeId = in.readString();
        typeName = in.readString();
        hide = in.readByte() != 0;
        lastReplyCount = in.readInt();
    private Thread(Parcel source) {
        id = source.readString();
        title = source.readString();
        replies = source.readString();
        permission = source.readInt();
        author = source.readString();
        authorid = source.readInt();
        fid = source.readString();
        typeId = source.readString();
        hide = source.readByte() != 0;
        lastReplyCount = source.readInt();
    }

    public static final Creator<Thread> CREATOR = new Creator<Thread>() {
        @Override
        public Thread createFromParcel(Parcel in) {
            return new Thread(in);
        }

        @Override
        public Thread[] newArray(int size) {
            return new Thread[size];
        }
    };

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

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public int getLastReplyCount() {
        return lastReplyCount;
    }

    public void setLastReplyCount(int lastReplyCount) {
        this.lastReplyCount = lastReplyCount;
    }

    public String getTypeId() {
        return typeId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(replies);
        dest.writeInt(permission);
        dest.writeString(author);
        dest.writeInt(authorid);
        dest.writeString(fid);
        dest.writeString(typeId);
        dest.writeByte((byte) (hide ? 1 : 0));
        dest.writeInt(lastReplyCount);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thread)) return false;
        Thread thread = (Thread) o;
        return permission == thread.permission &&
                authorId == thread.authorId &&
                displayOrder == thread.displayOrder &&
                hide == thread.hide &&
                hide == thread.hide &&
                lastReplyCount == thread.lastReplyCount &&
                Objects.equal(id, thread.id) &&
                Objects.equal(title, thread.title) &&
                Objects.equal(replies, thread.replies) &&
                Objects.equal(author, thread.author) &&
                Objects.equal(typeId, thread.typeId) &&
                Objects.equal(typeName, thread.typeName);
                Objects.equal(author, thread.author) &&
                Objects.equal(fid, thread.fid) &&
                Objects.equal(typeId, thread.typeId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, replies, permission, author, authorid, fid, typeId, hide, lastReplyCount);
    }

    @Override
    public Thread clone() {
        Thread o = null;
        try {
            o = (Thread) super.clone();
        } catch (CloneNotSupportedException e) {
            L.e(TAG, e);
        } catch (ClassCastException e) {
            L.e(TAG, e);
        }
        return o;
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thread thread = (Thread) o;
        return Objects.equal(id, thread.id) &&
                Objects.equal(title, thread.title) &&
                Objects.equal(author, thread.author) &&
                Objects.equal(authorId, thread.authorId);
    }

    public int getReliesCount() {
        try {
            return Integer.parseInt(getReplies());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(replies);
        dest.writeInt(permission);
        dest.writeString(author);
        dest.writeInt(authorId);
        dest.writeInt(displayOrder);
        dest.writeString(typeId);
        dest.writeString(typeName);
        dest.writeByte((byte) (hide ? 1 : 0));
        dest.writeInt(lastReplyCount);
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
