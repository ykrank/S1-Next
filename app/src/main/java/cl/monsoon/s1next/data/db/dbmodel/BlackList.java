package cl.monsoon.s1next.data.db.dbmodel;

import android.support.annotation.IntDef;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

import cl.monsoon.s1next.R;

/**
 * 黑名单数据库
 * 使用了ActiveAndroid
 * Created by AdminYkrank on 2016/2/23.
 */
@Table(name = "BlackList")
public class BlackList extends Model {
    private static final String Format = "yyyy-MM-dd HH:mm";
    
    @IntDef({NORMAL, HIDE_POST, DEL_POST})
    public @interface PostFLag {
    }

    @IntDef({NORMAL, HIDE_FORUM, DEL_FORUM})
    public @interface ForumFLag {
    }
    
    public static final int NORMAL = 0;
    public static final int HIDE_POST = 1;
    public static final int DEL_POST = 2;
    public static final int HIDE_FORUM = 3;
    public static final int DEL_FORUM = 4;

    /**
     * Id
     */
    @Column(name = "AuthorId")
    public int authorid;
    
    /**
     * 用户名
     */
    @Column(name = "Author")
    public String author;

    /**
     * 回复的屏蔽状态
     */
    @Column(name = "Post")
    public int post = NORMAL;

    /**
     * 主题的屏蔽状态
     */
    @Column(name = "Forum")
    public int forum = NORMAL;

    /**
     * 屏蔽时的备注
     */
    @Column(name = "Remark")
    public String remark;

    /**
     * 屏蔽时的时间
     */
    @Column(name = "Timestamp")
    public long timestamp;

    /**
     * 是否已同步
     */
    @Column(name = "Upload")
    public boolean upload;

    public BlackList() {
        super();
    }

    public BlackList(int authorid, String name, @PostFLag int post, @ForumFLag int forum) {
        super();
        this.authorid = authorid;
        this.author = name;
        this.post = post;
        this.forum = forum;
        this.remark = "";
        this.timestamp = System.currentTimeMillis();
        this.upload = false;
    }
    
    public void copyFrom(BlackList bList){
        this.authorid = bList.authorid;
        this.author = bList.author;
        this.post = bList.post;
        this.forum = bList.forum;
        this.remark = bList.remark;
        this.timestamp = bList.timestamp;
        this.upload = bList.upload;
    }
    
    public int getPostres(){
        switch (post){
            case HIDE_POST:
                return R.string.blacklist_flag_hide;
            case DEL_POST:
                return R.string.blacklist_flag_del;
            default:
                return R.string.blacklist_flag_normal;
        }
    }

    public int getForumres(){
        switch (forum){
            case HIDE_FORUM:
                return R.string.blacklist_flag_hide;
            case DEL_FORUM:
                return R.string.blacklist_flag_del;
            default:
                return R.string.blacklist_flag_normal;
        }
    }
    
    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        return sdf.format(new Date(timestamp));
    }
}