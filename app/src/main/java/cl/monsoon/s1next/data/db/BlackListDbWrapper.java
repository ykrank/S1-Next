package cl.monsoon.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.Select;

import java.util.List;

import cl.monsoon.s1next.data.db.dbmodel.BlackList;

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
    
    public BlackList getBlackList(int id, String name){
        BlackList oBlackList = null;
        if (id > 0){
            oBlackList = new Select().from(BlackList.class)
                    .where("Authorid = ?", id)
                    .executeSingle();
        }else if (name != null && !TextUtils.isEmpty(name)){
            oBlackList = new Select().from(BlackList.class)
                    .where("Author = ?", name)
                    .executeSingle();
        }
        return oBlackList;
    }
    
    @BlackList.ForumFLag
    public int getForumFlag(int id, String name){
        BlackList oBlackList = getBlackList(id, name);
        if (oBlackList != null) return oBlackList.forum;
        return BlackList.NORMAL;
    }

    @BlackList.PostFLag
    public int getPostFlag(int id, String name){
        BlackList oBlackList = getBlackList(id, name);
        if (oBlackList != null) return oBlackList.post;
        return  BlackList.NORMAL;
    }
    
    public void saveBlackList(@NonNull BlackList blackList){
        if (blackList.authorid > 0){
            BlackList oBlackList = new Select().from(BlackList.class)
                    .where("Authorid = ?", blackList.authorid)
                    .executeSingle();
            if (oBlackList == null) oBlackList = new BlackList();
            oBlackList.copyFrom(blackList);
            oBlackList.timestamp = System.currentTimeMillis();
            oBlackList.save();
            return;
        }
        if (blackList.author != null && !TextUtils.isEmpty(blackList.author)){
            BlackList oBlackList = new Select().from(BlackList.class)
                    .where("Author = ?", blackList.author)
                    .executeSingle();
            if (oBlackList == null) oBlackList = new BlackList();
            oBlackList.copyFrom(blackList);
            oBlackList.timestamp = System.currentTimeMillis();
            oBlackList.save();
        }
    }

    public void delBlackList(@NonNull BlackList blackList){
        if (blackList.authorid > 0){
            BlackList oBlackList = new Select().from(BlackList.class)
                    .where("Authorid = ?", blackList.authorid)
                    .executeSingle();
            if (oBlackList != null) 
                oBlackList.delete();
            return;
        }
        if (blackList.author != null && !TextUtils.isEmpty(blackList.author)){
            BlackList oBlackList = new Select().from(BlackList.class)
                    .where("Author = ?", blackList.author)
                    .executeSingle();
            if (oBlackList != null)
                oBlackList.delete();
        }
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
        saveBlackList(blackList);
    }

    public void delDefaultBlackList(int authorid, String author){
        BlackList blackList = new BlackList();
        blackList.authorid = authorid;
        blackList.author = author;
        delBlackList(blackList);
    }
}
