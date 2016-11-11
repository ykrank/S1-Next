package me.ykrank.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.Select;

import java.util.List;

import me.ykrank.s1next.data.db.dbmodel.BlackList;

/**
 * 对黑名单数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class BlackListDbWrapper {
    private static BlackListDbWrapper blackListWrapper;
    
    private BlackListDbWrapper(){}
    
    public static BlackListDbWrapper getInstance(){
        if (blackListWrapper == null) blackListWrapper = new BlackListDbWrapper();
        return  blackListWrapper;
    }
    
    public List<BlackList> getAllBlackList(int limit, int offset){
        return new Select().from(BlackList.class)
                .limit(limit)
                .offset(offset)
                .execute();
    }

    public Cursor getBlackListCursor(){
        String tableName = Cache.getTableInfo(BlackList.class).getTableName();
        // Query all items without any conditions
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id")
                .from(BlackList.class)
                .toSql();
        // Execute query on the underlying ActiveAndroid SQLite database
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }

    public BlackList fromCursor(@NonNull Cursor cursor) {
        BlackList blackList = new BlackList();
        blackList.loadFromCursor(cursor);
        return blackList;
    }

    /**
     * 默认情况下的黑名单查找。如果用户id合法则优先id，否则查找用户name
     * @param id
     * @param name
     * @return
     */
    public BlackList getBlackListDefault(int id, String name){
        BlackList oBlackList = null;
        if (id > 0){
            oBlackList = getBlackListWithAuthorId(id);
        }else if (name != null && !TextUtils.isEmpty(name)){
            oBlackList = getBlackListWithAuthorName(name);
        }
        return oBlackList;
    }

    /**
     * 根据用户id查找记录
     * @param id
     * @return
     */
    public BlackList getBlackListWithAuthorId(int id){
        return new Select().from(BlackList.class)
                .where("Authorid = ?", id)
                .executeSingle();
    }

    /**
     * 根据用户名查找记录
     * @param name
     * @return
     */
    public BlackList getBlackListWithAuthorName(String name){
        return new Select().from(BlackList.class)
                .where("Author = ?", name)
                .executeSingle();
    }
    
    @BlackList.ForumFLag
    public int getForumFlag(int id, String name){
        BlackList oBlackList = getBlackListDefault(id, name);
        if (oBlackList != null) return oBlackList.forum;
        return BlackList.NORMAL;
    }

    @BlackList.PostFLag
    public int getPostFlag(int id, String name){
        BlackList oBlackList = getBlackListDefault(id, name);
        if (oBlackList != null) return oBlackList.post;
        return  BlackList.NORMAL;
    }
    
    public void saveBlackList(@NonNull BlackList blackList){
        BlackList oBlackList = getBlackListDefault(blackList.authorid, blackList.author);
        if (oBlackList == null) {
            blackList.save();
        }else{
            oBlackList.copyFrom(blackList);
            oBlackList.save();
        }
    }

    public void delBlackList(@NonNull BlackList blackList){
        BlackList oBlackList = getBlackListDefault(blackList.authorid, blackList.author);
        if (oBlackList != null)
            oBlackList.delete();
    }

    public void delBlackLists(List<BlackList> blackLists) {
        ActiveAndroid.beginTransaction();
        try {
            for (BlackList blacklist : blackLists) {
                blacklist.delete();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void saveDefaultBlackList(int authorid, String author){
        BlackList blackList = new BlackList();
        blackList.authorid = authorid;
        blackList.author = author;
        blackList.post = BlackList.HIDE_POST;
        blackList.forum = BlackList.HIDE_FORUM;
        blackList.timestamp = System.currentTimeMillis();
        saveBlackList(blackList);
    }

    public void delDefaultBlackList(int authorid, String author){
        BlackList blackList = new BlackList();
        blackList.authorid = authorid;
        blackList.author = author;
        delBlackList(blackList);
    }
}
