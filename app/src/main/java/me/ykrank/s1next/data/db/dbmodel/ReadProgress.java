package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

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
    /**
     * 加载进度处于空闲状态
     */
    public static final int FREE = 0;
    /**
     * 加载进度处于滑动到指定页之前
     */
    public static final int BEFORE_SCROLL_PAGE = 1;
    /**
     * 加载进度处于滑动到指定位置之前
     */
    public static final int BEFORE_SCROLL_POSITION = 2;
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
    @ScrollState
    private int scrollState;

    @SuppressWarnings("WrongConstant")
    protected ReadProgress(Parcel in) {
        threadId = in.readString();
        page = in.readInt();
        position = in.readInt();
        timestamp = in.readLong();
        scrollState = in.readInt();
    }

    public ReadProgress() {
        super();
    }

    public ReadProgress(String threadId, int page, int position) {
        super();
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = System.currentTimeMillis();
        this.scrollState = FREE;
    }

    @Generated(hash = 459708891)
    public ReadProgress(Long id, String threadId, int page, int position, long timestamp,
                        int scrollState) {
        this.id = id;
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = timestamp;
        this.scrollState = scrollState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(threadId);
        dest.writeInt(page);
        dest.writeInt(position);
        dest.writeLong(timestamp);
        dest.writeInt(scrollState);
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

    public int getScrollState() {
        return scrollState;
    }

    public void setScrollState(int scrollState) {
        this.scrollState = scrollState;
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

    /**
     * 加载进度
     */
    @IntDef({FREE, BEFORE_SCROLL_PAGE, BEFORE_SCROLL_POSITION})
    public @interface ScrollState {
    }
}
