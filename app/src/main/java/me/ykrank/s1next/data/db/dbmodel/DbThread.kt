package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

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
@Entity(nameInDb = "DbThread")
public class DbThread implements Parcelable {
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
     * 最后一次访问时的回复数
     */
    @Property(nameInDb = "LastCountWhenView")
    private int lastCountWhenView;
    /**
     * 更新时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    @Generated(hash = 1044313799)
    public DbThread(Long id, int threadId, int lastCountWhenView, long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.lastCountWhenView = lastCountWhenView;
        this.timestamp = timestamp;
    }

    public DbThread(int threadId, int lastCountWhenView) {
        this.threadId = threadId;
        this.lastCountWhenView = lastCountWhenView;
        this.timestamp = System.currentTimeMillis();
    }

    public DbThread() {
        this.timestamp = System.currentTimeMillis();
    }

    protected DbThread(Parcel in) {
        boolean hasId = in.readByte() == 1;
        if (hasId) {
            id = in.readLong();
        }
        threadId = in.readInt();
        lastCountWhenView = in.readInt();
        timestamp = in.readLong();
    }

    public static final Creator<DbThread> CREATOR = new Creator<DbThread>() {
        @Override
        public DbThread createFromParcel(Parcel in) {
            return new DbThread(in);
        }

        @Override
        public DbThread[] newArray(int size) {
            return new DbThread[size];
        }
    };

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getLastCountWhenView() {
        return lastCountWhenView;
    }

    public void setLastCountWhenView(int lastCountWhenView) {
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
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(threadId);
        dest.writeInt(lastCountWhenView);
        dest.writeLong(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbThread dbThread = (DbThread) o;
        return lastCountWhenView == dbThread.lastCountWhenView &&
                timestamp == dbThread.timestamp &&
                Objects.equal(id, dbThread.id) &&
                Objects.equal(threadId, dbThread.threadId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, threadId, lastCountWhenView, timestamp);
    }

    public void copyFrom(DbThread oDbThread) {
        this.threadId = oDbThread.threadId;
        this.lastCountWhenView = oDbThread.lastCountWhenView;
        this.timestamp = oDbThread.timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }
}
