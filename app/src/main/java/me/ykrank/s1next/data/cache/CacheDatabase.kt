package me.ykrank.s1next.data.cache

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import me.ykrank.s1next.data.cache.dao.CacheDao
import me.ykrank.s1next.data.cache.dao.CacheGroupDao
import me.ykrank.s1next.data.cache.dbmodel.Cache
import me.ykrank.s1next.data.cache.dbmodel.CacheGroup

@Database(
    version = 2,
    entities = [
        Cache::class,
        CacheGroup::class,
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cache(): CacheDao

    abstract fun cacheGroup(): CacheGroupDao

    companion object{
        const val DB_NAME= "cache.db"
    }
}

