package me.ykrank.s1next.widget;

import android.content.Context;
import android.widget.Toast;

import org.greenrobot.greendao.database.Database;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.dbmodel.DaoMaster;
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
        if (oldVersion < 3) {
            db.beginTransaction();
            try {
                // TODO: 2017/1/18  数据库更新
                String tempDbBlackList = DB_BLACKLIST + "_temp";
                String tempDbReadProgress = DB_READ_PROGRESS + "_temp";
                // rename the old table
                db.execSQL("ALTER TABLE " + DB_BLACKLIST + " RENAME TO " + tempDbBlackList + ";");
                db.execSQL("ALTER TABLE " + DB_READ_PROGRESS + " RENAME TO " + tempDbReadProgress + ";");
                //create new table
                onCreate(db);
                //copy old data  to new table

                //create temp table. as BlackListDao#createTable
                db.execSQL("CREATE TABLE \"" + tempDbBlackList + "\" (" + //
                        "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                        "\"AuthorId\" INTEGER NOT NULL ," + // 1: authorId
                        "\"Author\" TEXT," + // 2: author
                        "\"Post\" INTEGER NOT NULL ," + // 3: post
                        "\"Forum\" INTEGER NOT NULL ," + // 4: forum
                        "\"Remark\" TEXT," + // 5: remark
                        "\"Timestamp\" INTEGER NOT NULL ," + // 6: timestamp
                        "\"Upload\" INTEGER NOT NULL );"); // 7: upload
                db.execSQL("CREATE TABLE \"" + tempDbReadProgress + "\" (" + //
                        "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                        "\"ThreadId\" TEXT," + // 1: threadId
                        "\"Page\" INTEGER NOT NULL ," + // 2: page
                        "\"Position\" INTEGER NOT NULL ," + // 3: position
                        "\"Timestamp\" INTEGER NOT NULL );"); // 4: timestamp
                //copy old data to temp table
                db.execSQL("insert into \"" + tempDbBlackList + "\" select * from \"" + DB_BLACKLIST + "\";");
                db.execSQL("insert into \"" + tempDbReadProgress + "\" select * from \"" + DB_READ_PROGRESS + "\";");
                //drop all old tables if exists
                DaoMaster.dropAllTables(db, true);
                //create new table
                onCreate(db);
                //copy old data from temp table to new table

                //drop temp table

                db.setTransactionSuccessful();
            } catch (Throwable e) {
                L.report(e);
                Toast.makeText(App.get(), R.string.database_update_error, Toast.LENGTH_LONG).show();
            } finally {
                db.endTransaction();
            }
        }
    }
}
