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

    public AppDaoOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        L.e("DB upgrade##oldVersion:" + oldVersion + ",newVersion:" + newVersion);
        if (oldVersion == 1) {
            update1toNew(db, oldVersion, newVersion);
            return;
        }
        if (oldVersion == 2) {
            update2toNew(db, oldVersion, newVersion);
            return;
        }
        update3to4(db, oldVersion, newVersion);
        update4to5(db, oldVersion, newVersion);
    }

    /**
     * drop old blacklist table, and create all table
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    private void update1toNew(Database db, int oldVersion, int newVersion) {
        BlackListDao.dropTable(db, true);
        onCreate(db);
    }

    /**
     * update from ActiveAndroid to GreenDAO.
     * create all table, and copy old blacklist data
     */
    private void update2toNew(Database db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            String tempDbBlackList = "BlackList_temp";
            String tempDbReadProgress = "ReadProgress_temp";
            // rename the old table
            db.execSQL("ALTER TABLE BlackList RENAME TO " + tempDbBlackList + ";");
            db.execSQL("ALTER TABLE ReadProgress RENAME TO " + tempDbReadProgress + ";");
            //create new table
            onCreate(db);
            //copy old data  to new table
            db.execSQL("INSERT INTO BlackList(AuthorId,Author,Post,Forum,Remark,Timestamp,Upload) " +
                    "SELECT AuthorId,Author,Post,Forum,Remark,Timestamp,Upload FROM " + tempDbBlackList + ";");
            db.execSQL("INSERT INTO ReadProgress(ThreadId,Page,Position,Timestamp) " +
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
        if (oldVersion <= 3) {
            HistoryDao.createTable(db, false);
        }
    }

    /**
     * add {@link me.ykrank.s1next.data.db.dbmodel.ReadProgress#offset} in table {@link me.ykrank.s1next.data.db.dbmodel.ReadProgress}
     */
    private void update4to5(Database db, int oldVersion, int newVersion) {
        if (oldVersion <= 4) {
            try {
                // add column `offset`
                db.execSQL("ALTER TABLE ReadProgress ADD COLUMN `offset` INTEGER default 0;");
            } catch (Throwable e) {
                L.report(e);
                Toast.makeText(App.get(), R.string.database_update_error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
