package me.ykrank.s1next.data.db.dbmodel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

/**
 * Post read history
 */
@Entity(nameInDb = "History")
public class History {
    @Id(autoincrement = true)
    private Long id;
    /**
     * 帖子ID
     */
    @Property(nameInDb = "ThreadId")
    @Index(unique = true)
    private int threadId;
    @Property(nameInDb = "Title")
    private String title;
    /**
     * 更新时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    @Generated(hash = 1091300362)
    public History(Long id, int threadId, String title, long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.title = title;
        this.timestamp = timestamp;
    }

    @Generated(hash = 869423138)
    public History() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
