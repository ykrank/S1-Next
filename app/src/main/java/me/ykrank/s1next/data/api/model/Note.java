package me.ykrank.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ykrank.s1next.data.SameItem;

/**
 * Created by ykrank on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Note implements Parcelable, SameItem {
    @JsonProperty("author")
    private String author;
    @JsonProperty("authorid")
    private String authorId;
    @JsonProperty("dateline")
    private long dateline;
    @JsonProperty("id")
    private String id;
    @JsonIgnore
    private boolean isNew;
    @JsonIgnore
    private String note;
    //eg forum.php?mod=redirect&goto=findpost&ptid=1220112&pid=1
    @JsonIgnore
    private String url;
    @JsonIgnore
    private String content;

    @JsonCreator
    public Note(@JsonProperty("note") String note) {
        this.note = note;
        //eg <a href="home.php?mod=space&uid=1">someone</a> 回复了您的帖子 <a href="forum.php?mod=redirect&goto=findpost&ptid=1220112&pid=1" target="_blank">【Android】 s1Next-鹅版-v0.7.2（群522433035）</a> &nbsp; <a href="forum.php?mod=redirect&goto=findpost&pid=34692327&ptid=1220112" target="_blank" class="lit">查看</a>
        Pattern pattern = Pattern.compile("<a href=\"(forum\\.php\\?mod=redirect&goto=findpost.+?)\"");
        Matcher matcher = pattern.matcher(note);
        if (matcher.find()) {
            url = matcher.group(1);
        }
        pattern = Pattern.compile("target=\"_blank\">(.+)</a> &nbsp;");
        matcher = pattern.matcher(note);
        if (matcher.find()) {
            content = matcher.group(1);
        }
    }

    protected Note(Parcel in) {
        author = in.readString();
        authorId = in.readString();
        dateline = in.readLong();
        id = in.readString();
        isNew = in.readByte() != 0;
        note = in.readString();
        url = in.readString();
        content = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @JsonSetter("new")
    public void setNew(int aNew) {
        isNew = aNew > 0;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(authorId);
        dest.writeLong(dateline);
        dest.writeString(id);
        dest.writeByte((byte) (isNew ? 1 : 0));
        dest.writeString(note);
        dest.writeString(url);
        dest.writeString(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note1 = (Note) o;
        return dateline == note1.dateline &&
                isNew == note1.isNew &&
                Objects.equal(author, note1.author) &&
                Objects.equal(authorId, note1.authorId) &&
                Objects.equal(id, note1.id) &&
                Objects.equal(note, note1.note) &&
                Objects.equal(url, note1.url) &&
                Objects.equal(content, note1.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(author, authorId, dateline, id, isNew, note, url, content);
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note1 = (Note) o;
        return Objects.equal(id, note1.id);
    }
}
