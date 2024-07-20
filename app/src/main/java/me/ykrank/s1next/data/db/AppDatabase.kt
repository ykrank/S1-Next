package me.ykrank.s1next.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.dao.BlackListDao
import me.ykrank.s1next.data.db.dao.BlackWordDao
import me.ykrank.s1next.data.cache.CacheDao
import me.ykrank.s1next.data.db.dao.HistoryDao
import me.ykrank.s1next.data.db.dao.LoginUserDao
import me.ykrank.s1next.data.db.dao.ReadProgressDao
import me.ykrank.s1next.data.db.dao.ThreadDao
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.data.cache.Cache
import me.ykrank.s1next.data.db.dbmodel.DbThread
import me.ykrank.s1next.data.db.dbmodel.History
import me.ykrank.s1next.data.db.dbmodel.LoginUser
import me.ykrank.s1next.data.db.dbmodel.ReadProgress

@Database(
    version = 9,
    entities = [
        BlackList::class,
        BlackWord::class,
        DbThread::class,
        History::class,
        ReadProgress::class,
        LoginUser::class,
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blacklist(): BlackListDao

    abstract fun blackWord(): BlackWordDao

    abstract fun history(): HistoryDao

    abstract fun readProgress(): ReadProgressDao

    abstract fun thread(): ThreadDao

    abstract fun loginUser(): LoginUserDao
}

