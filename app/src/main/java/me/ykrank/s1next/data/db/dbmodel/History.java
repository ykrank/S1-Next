package me.ykrank.s1next.data.db.dbmodel;

import org.greenrobot.greendao.annotation.Entity;
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
}
