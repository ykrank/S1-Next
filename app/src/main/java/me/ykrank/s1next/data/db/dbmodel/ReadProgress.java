package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AdminYkrank on 2016/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@Entity(nameInDb = "ReadProgress")
public class ReadProgress implements Parcelable {
    public static final Creator<ReadProgress> CREATOR = new Creator<ReadProgress>() {
        @Override
        public ReadProgress createFromParcel(Parcel in) {
            return new ReadProgress(in);
        }

        @Override
        public ReadProgress[] newArray(int size) {
            return new ReadProgress[size];
        }
    };
    private static final String TimeFormat = "yyyy-MM-dd HH:mm";
    @Id(autoincrement = true)
    private Long id;
    /**
     * 帖子ID
     */
    @Property(nameInDb = "ThreadId")
    @Index(unique = true)
    private String threadId;
    /**
     * 页数
     */
    @Property(nameInDb = "Page")
    private int page;
    /**
     * 位置
     */
    @Property(nameInDb = "Position")
    private int position;
    /**
     * 更新时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    @SuppressWarnings("WrongConstant")
    protected ReadProgress(Parcel in) {
        id = in.readLong();
        threadId = in.readString();
        page = in.readInt();
        position = in.readInt();
        timestamp = in.readLong();
    }

    public ReadProgress() {
        this.timestamp = System.currentTimeMillis();
    }

    public ReadProgress(String threadId, int page, int position) {
        super();
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = System.currentTimeMillis();
    }

    @Generated(hash = 1629999723)
    public ReadProgress(Long id, String threadId, int page, int position, long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(threadId);
        dest.writeInt(page);
        dest.writeInt(position);
        dest.writeLong(timestamp);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static String getTimeFormat() {
        return TimeFormat;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void copyFrom(ReadProgress oReadProgress) {
        this.threadId = oReadProgress.threadId;
        this.page = oReadProgress.page;
        this.position = oReadProgress.position;
        this.timestamp = oReadProgress.timestamp;
    }

    @Override
    public String toString() {
        return "ReadProgress{" +
                "threadId=" + threadId +
                ", page=" + page +
                ", position=" + position +
                ", timestamp=" + getTime() +
                '}';
    }


}
