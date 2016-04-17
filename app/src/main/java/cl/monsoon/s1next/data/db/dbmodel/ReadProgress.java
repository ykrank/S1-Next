package cl.monsoon.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AdminYkrank on 2016/4/16.
 */
@Table(name = "ReadProgress")
public class ReadProgress extends Model implements Parcelable{
    private static final String TimeFormat = "yyyy-MM-dd HH:mm";

    /**
     * 帖子ID
     */
    @Column(name = "ThreadId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String threadId;

    /**
     * 页数
     */
    @Column(name = "Page")
    public int page;

    /**
     * 位置
     */
    @Column(name = "Position")
    public int position;

    /**
     * 更新时间
     */
    @Column(name = "Timestamp")
    public long timestamp;

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

    @SuppressWarnings("WrongConstant")
    protected ReadProgress(Parcel in) {
        threadId = in.readString();
        page = in.readInt();
        position = in.readInt();
        timestamp = in.readLong();
        scrollProgress = in.readInt();
    }

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
        dest.writeInt(scrollProgress);
    }

    /**
     * 加载进度
     */
    @IntDef({FREE, BEFORE_SCROLL_PAGE, BEFORE_SCROLL_POSITION})
    public @interface ScrollProgress{};
    
    @ScrollProgress
    public int scrollProgress;

    public ReadProgress(){
        super();
    }
    
    public ReadProgress(String threadId, int page, int position){
        super();
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = System.currentTimeMillis();
        this.scrollProgress = FREE;
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat);
        return sdf.format(new Date(timestamp));
    }
    
    public void copyFrom(ReadProgress oReadProgress){
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
