package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import paperparcel.PaperParcel;

/**
 * Created by AdminYkrank on 2016/4/16.
 */
// TODO: 2017/6/3  GreenDao model do not support kotlin now
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@Entity(nameInDb = "ReadProgress")
@PaperParcel
public class ReadProgress implements Parcelable {
    public static final Creator<ReadProgress> CREATOR = PaperParcelReadProgress.CREATOR;

    private static final String TimeFormat = "yyyy-MM-dd HH:mm";
    @Id(autoincrement = true)
    @Nullable
    private Long id;
    /**
     * 帖子ID
     */
    @Property(nameInDb = "ThreadId")
    @Index(unique = true)
    private int threadId;
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
     * Item 对应View和上边界的偏移
     */
    @Property(nameInDb = "Offset")
    private int offset;
    /**
     * 更新时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    public ReadProgress() {
        this.timestamp = System.currentTimeMillis();
    }

    public ReadProgress(int threadId, int page, int position, int offset) {
        super();
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.offset = offset;
        this.timestamp = System.currentTimeMillis();
    }

    @Generated(hash = 411640930)
    public ReadProgress(Long id, int threadId, int page, int position, int offset,
                        long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.offset = offset;
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        PaperParcelReadProgress.writeToParcel(this, dest, flags);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static String getTimeFormat() {
        return TimeFormat;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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
        this.offset = oReadProgress.offset;
        this.timestamp = oReadProgress.timestamp;
    }

    @Override
    public String toString() {
        return "ReadProgress{" +
                "id=" + id +
                ", threadId=" + threadId +
                ", page=" + page +
                ", position=" + position +
                ", offset=" + offset +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadProgress that = (ReadProgress) o;
        return threadId == that.threadId &&
                page == that.page &&
                position == that.position &&
                offset == that.offset &&
                timestamp == that.timestamp &&
                Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, threadId, page, position, offset, timestamp);
    }
}
