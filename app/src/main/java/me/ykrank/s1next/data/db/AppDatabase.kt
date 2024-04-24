package me.ykrank.s1next.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.ykrank.s1next.data.db.dao.BlackListDao
import me.ykrank.s1next.data.db.dao.BlackWordDao
import me.ykrank.s1next.data.db.dao.HistoryDao
import me.ykrank.s1next.data.db.dao.ReadProgressDao
import me.ykrank.s1next.data.db.dao.ThreadDao
import me.ykrank.s1next.data.db.dbmodel.BlackList

@Database(
    version = 7,
    entities = [BlackList::class],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blacklist(): BlackListDao

    abstract fun blackWord(): BlackWordDao

    abstract fun history(): HistoryDao

    abstract fun readProgress(): ReadProgressDao

    abstract fun thread(): ThreadDao
}
