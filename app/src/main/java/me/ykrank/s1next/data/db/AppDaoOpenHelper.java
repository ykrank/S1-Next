package me.ykrank.s1next.data.db;

import android.content.Context;
import android.widget.Toast;

import org.greenrobot.greendao.database.Database;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.dbmodel.BlackListDao;
import me.ykrank.s1next.data.db.dbmodel.DaoMaster;
import me.ykrank.s1next.data.db.dbmodel.HistoryDao;
import me.ykrank.s1next.util.L;

/**
 * Created by ykrank on 2017/1/16.
 */

public class AppDaoOpenHelper extends DaoMaster.OpenHelper {
    public static final String DB_BLACKLIST = "BlackList";
    public static final String DB_READ_PROGRESS = "ReadProgress";

    public AppDaoOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        L.e("DB upgrade##oldVersion:" + oldVersion + ",newVersion:" + newVersion);
        if (oldVersion == 1){
            update1toNew(db, oldVersion, newVersion);
            return;
        }
        if (oldVersion == 2) {
            update2toNew(db, oldVersion, newVersion);
            return;
        }
        if (oldVersion == 3) {
            update3to4(db, oldVersion, newVersion);
        }
    }

    private void update1toNew(Database db, int oldVersion, int newVersion) {
        BlackListDao.dropTable(db, true);
        onCreate(db);
    }

    /**
    update from ActiveAndroid to GreenDAO
     */
    private void update2toNew(Database db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            String tempDbBlackList = DB_BLACKLIST + "_temp";
            String tempDbReadProgress = DB_READ_PROGRESS + "_temp";
            // rename the old table
            db.execSQL("ALTER TABLE " + DB_BLACKLIST + " RENAME TO " + tempDbBlackList + ";");
            db.execSQL("ALTER TABLE " + DB_READ_PROGRESS + " RENAME TO " + tempDbReadProgress + ";");
            //create new table
            onCreate(db);
            //copy old data  to new table
            db.execSQL("INSERT INTO " + DB_BLACKLIST + "(AuthorId,Author,Post,Forum,Remark,Timestamp,Upload) " +
                    "SELECT AuthorId,Author,Post,Forum,Remark,Timestamp,Upload FROM " + tempDbBlackList + ";");
            db.execSQL("INSERT INTO " + DB_READ_PROGRESS + "(ThreadId,Page,Position,Timestamp) " +
                    "SELECT ThreadId,Page,Position,Timestamp FROM " + tempDbReadProgress + ";");
            //drop old table
            db.execSQL("DROP TABLE " + tempDbBlackList + ";");
            db.execSQL("DROP TABLE " + tempDbReadProgress + ";");
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            L.report(e);
            Toast.makeText(App.get(), R.string.database_update_error, Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * add table {@link me.ykrank.s1next.data.db.dbmodel.History}
     */
    private void update3to4(Database db, int oldVersion, int newVersion) {
        HistoryDao.createTable(db, false);
    }
}
