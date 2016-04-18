package me.ykrank.s1next.data.db;

import android.support.annotation.NonNull;

import com.activeandroid.query.Select;

import me.ykrank.s1next.data.db.dbmodel.ReadProgress;

/**
 * 对黑名单数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class ReadProgressDbWrapper {
    private static ReadProgressDbWrapper dbWrapper;
    
    private ReadProgressDbWrapper(){}
    
    public static ReadProgressDbWrapper getInstance(){
        if (dbWrapper == null) dbWrapper = new ReadProgressDbWrapper();
        return dbWrapper;
    }
    
    public ReadProgress getWithThreadId(String threadId){
        return new Select().from(ReadProgress.class)
                .where("ThreadId = ?", threadId)
                .executeSingle();
    }
    
    public Long saveReadProgress(@NonNull ReadProgress readProgress){
        ReadProgress oReadProgress = getWithThreadId(readProgress.threadId);
        if (oReadProgress == null) {
            return readProgress.save();
        }else{
            oReadProgress.copyFrom(readProgress);
            return oReadProgress.save();
        }
    }

    public void delReadProgress(String threadId){
        ReadProgress oReadProgress = getWithThreadId(threadId);
        if (oReadProgress != null) {
            oReadProgress.delete();
        }
    }
 
}
