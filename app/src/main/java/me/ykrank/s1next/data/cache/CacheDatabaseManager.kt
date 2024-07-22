package me.ykrank.s1next.data.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.db.AppDatabaseManager

interface CacheDatabaseManager {
    fun getOrBuildDb(): CacheDatabase

    fun close()

    fun runAsync(runnable: Runnable)

    val version
        get() = getOrBuildDb().openHelper.readableDatabase.version
}

class CacheDatabaseManagerImpl(
    applicationContext: Context,
    private val appDatabaseManager: AppDatabaseManager
) : CacheDatabaseManager {

    val builder = Room.databaseBuilder(
        applicationContext,
        CacheDatabase::class.java, CacheDatabase.DB_NAME,
    ).setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)

    @Volatile
    var database: CacheDatabase? = null


    override fun getOrBuildDb(): CacheDatabase {
        return database ?: synchronized(this) {
            database ?: builder.build().also { database = it }
        }
    }

    override fun close() {
        database?.close()
        database = null
    }

    override fun runAsync(runnable: Runnable) {
        try {
            appDatabaseManager.runAsync(runnable)
        } catch (e: Exception) {
            L.report(e)
            if (L.showLog) {
                throw e
            }
        }
    }
}