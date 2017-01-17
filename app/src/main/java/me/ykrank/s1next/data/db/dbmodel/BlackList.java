package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.ykrank.s1next.R;

/**
 * 黑名单数据库
 * 使用了ActiveAndroid
 * Created by AdminYkrank on 2016/2/23.
 */
@Entity(nameInDb = "BlackList")
public class BlackList implements Parcelable {
    public static final Creator<BlackList> CREATOR = new Creator<BlackList>() {
        @Override
        public BlackList createFromParcel(Parcel in) {
            return new BlackList(in);
        }

        @Override
        public BlackList[] newArray(int size) {
            return new BlackList[size];
        }
    };
    public static final int NORMAL = 0;
    public static final int HIDE_POST = 1;
    public static final int DEL_POST = 2;
    public static final int HIDE_FORUM = 3;
    public static final int DEL_FORUM = 4;
    private static final String TimeFormat = "yyyy-MM-dd HH:mm";

    @Id(autoincrement = true)
    private Long id;

    /**
     * Id
     */
    @Property(nameInDb = "AuthorId")
    @Index(unique = true)
    private int authorId;
    /**
     * 用户名
     */
    @Property(nameInDb = "Author")
    @Index(unique = true)
    private String author;
    /**
     * 回复的屏蔽状态
     */
    @Property(nameInDb = "Post")
    @PostFLag
    private int post = NORMAL;
    /**
     * 主题的屏蔽状态
     */
    @Property(nameInDb = "Forum")
    @ForumFLag
    private int forum = NORMAL;
    /**
     * 屏蔽时的备注
     */
    @Property(nameInDb = "Remark")
    private String remark;
    /**
     * 屏蔽时的时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;
    /**
     * 是否已同步
     */
    @Property(nameInDb = "Upload")
    private boolean upload;

    @SuppressWarnings("WrongConstant")
    protected BlackList(Parcel in) {
        authorId = in.readInt();
        author = in.readString();
        post = in.readInt();
        forum = in.readInt();
        remark = in.readString();
        timestamp = in.readLong();
        upload = in.readByte() != 0;
    }

    public BlackList() {
        this.timestamp = System.currentTimeMillis();
    }

    public BlackList(int authorId, String name, @PostFLag int post, @ForumFLag int forum) {
        this.authorId = authorId;
        this.author = name;
        this.post = post;
        this.forum = forum;
        this.remark = "";
        this.timestamp = System.currentTimeMillis();
        this.upload = false;
    }

    @Generated(hash = 536415208)
    public BlackList(Long id, int authorId, String author, int post, int forum, String remark,
                     long timestamp, boolean upload) {
        this.id = id;
        this.authorId = authorId;
        this.author = author;
        this.post = post;
        this.forum = forum;
        this.remark = remark;
        this.timestamp = timestamp;
        this.upload = upload;
    }

    public void copyFrom(BlackList bList) {
        this.authorId = bList.authorId;
        this.author = bList.author;
        this.post = bList.post;
        this.forum = bList.forum;
        this.remark = bList.remark;
        this.timestamp = bList.timestamp;
        this.upload = bList.upload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static String getTimeFormat() {
        return TimeFormat;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @PostFLag
    public int getPost() {
        return post;
    }

    public void setPost(@PostFLag int post) {
        this.post = post;
    }

    @ForumFLag
    public int getForum() {
        return forum;
    }

    public void setForum(@ForumFLag int forum) {
        this.forum = forum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    @StringRes
    public int getPostRes() {
        switch (post) {
            case HIDE_POST:
                return R.string.blacklist_flag_hide;
            case DEL_POST:
                return R.string.blacklist_flag_del;
            default:
                return R.string.blacklist_flag_normal;
        }
    }

    @StringRes
    public int getForumRes() {
        switch (forum) {
            case HIDE_FORUM:
                return R.string.blacklist_flag_hide;
            case DEL_FORUM:
                return R.string.blacklist_flag_del;
            default:
                return R.string.blacklist_flag_normal;
        }
    }

    public boolean isForumHide() {
        return forum == HIDE_FORUM;
    }

    public boolean isPostHide() {
        return post == HIDE_POST;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(authorId);
        dest.writeString(author);
        dest.writeInt(post);
        dest.writeInt(forum);
        dest.writeString(remark);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (upload ? 1 : 0));
    }

    @Override
    public String toString() {
        return "BlackList{" +
                "authorId=" + authorId +
                ", author='" + author + '\'' +
                ", post=" + post +
                ", forum=" + forum +
                ", remark='" + remark + '\'' +
                ", timestamp=" + getTime() +
                ", upload=" + upload +
                '}';
    }

    public boolean getUpload() {
        return this.upload;
    }

    @IntDef({NORMAL, HIDE_POST, DEL_POST})
    public @interface PostFLag {

    }

    @IntDef({NORMAL, HIDE_FORUM, DEL_FORUM})
    public @interface ForumFLag {

    }
}