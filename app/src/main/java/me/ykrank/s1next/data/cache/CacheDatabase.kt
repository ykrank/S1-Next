package me.ykrank.s1next.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        Cache::class,
    ],
    exportSchema = true,
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cache(): CacheDao

    companion object{
        const val DB_NAME= "cache.db"
    }
}

