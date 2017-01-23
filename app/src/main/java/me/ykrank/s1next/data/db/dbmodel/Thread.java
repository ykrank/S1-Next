package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by ykrank on 2017/1/23.
 * 帖子相关信息数据库
 */
@Entity(nameInDb = "Thread")
public class Thread implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    /**
     * 帖子ID
     */
    @Property(nameInDb = "ThreadId")
    @Index(unique = true)
    private String threadId;
    /**
     * 最后一次访问时的回复数
     */
    @Property(nameInDb = "LastCountWhenView")
    private long lastCountWhenView;
    /**
     * 更新时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    @Generated(hash = 869534241)
    public Thread(Long id, String threadId, long lastCountWhenView,
                  long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.lastCountWhenView = lastCountWhenView;
        this.timestamp = timestamp;
    }

    public Thread(String threadId, long lastCountWhenView) {
        this.threadId = threadId;
        this.lastCountWhenView = lastCountWhenView;
        this.timestamp = System.currentTimeMillis();
    }

    public Thread() {
        this.timestamp = System.currentTimeMillis();
    }

    protected Thread(Parcel in) {
        id = in.readLong();
        threadId = in.readString();
        lastCountWhenView = in.readLong();
        timestamp = in.readLong();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public long getLastCountWhenView() {
        return lastCountWhenView;
    }

    public void setLastCountWhenView(long lastCountWhenView) {
        this.lastCountWhenView = lastCountWhenView;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
        dest.writeLong(lastCountWhenView);
        dest.writeLong(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thread thread = (Thread) o;
        return lastCountWhenView == thread.lastCountWhenView &&
                timestamp == thread.timestamp &&
                Objects.equal(id, thread.id) &&
                Objects.equal(threadId, thread.threadId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, threadId, lastCountWhenView, timestamp);
    }

    public void copyFrom(Thread oThread) {
        this.threadId = oThread.threadId;
        this.lastCountWhenView = oThread.lastCountWhenView;
        this.timestamp = oThread.timestamp;
    }
}
