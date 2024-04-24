package me.ykrank.s1next.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import me.ykrank.s1next.App
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.data.db.dao.BlackListDao
import me.ykrank.s1next.data.db.dao.BlackWordDao
import me.ykrank.s1next.data.db.dao.HistoryDao
import me.ykrank.s1next.data.db.dao.ReadProgressDao
import me.ykrank.s1next.data.db.dao.ThreadDao
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.data.db.dbmodel.DbThread
import me.ykrank.s1next.data.db.dbmodel.History
import me.ykrank.s1next.data.db.dbmodel.ReadProgress

@Database(
    version = 8,
    entities = [BlackList::class, BlackWord::class, DbThread::class, History::class, ReadProgress::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 7, to = 8),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blacklist(): BlackListDao

    abstract fun blackWord(): BlackWordDao

    abstract fun history(): HistoryDao

    abstract fun readProgress(): ReadProgressDao

    abstract fun thread(): ThreadDao

    companion object {
        @JvmStatic
        fun getVersion(): Int {
            return App.appComponent.appDatabaseManager.getOrBuildDb().openHelper.readableDatabase.version
        }
    }
}

