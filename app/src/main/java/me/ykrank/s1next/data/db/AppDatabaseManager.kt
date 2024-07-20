package me.ykrank.s1next.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import me.ykrank.s1next.BuildConfig
import java.util.concurrent.Executor
import java.util.concurrent.Executors

interface AppDatabaseManager {
    fun getOrBuildDb(): AppDatabase

    fun close()

    fun runAsync(runnable: Runnable)

    val version
        get() = getOrBuildDb().openHelper.readableDatabase.version
}

class AppDatabaseManagerImpl(applicationContext: Context) : AppDatabaseManager {

    private val executors: Executor = Executors.newSingleThreadExecutor()

    val builder = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, BuildConfig.DB_NAME,
    )   // 数据库升级版本7是在2.1.0-5（2018/12），因此之前旧版本直接破坏性重建可以接受
        .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6)

        // TRUNCATE模式，可以方便备份
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE).apply {
            if (!BuildConfig.DEBUG) {
                // TODO: 数据库操作禁止在主线程
                this.allowMainThreadQueries()
            }
        }

    @Volatile
    var database: AppDatabase? = null


    override fun getOrBuildDb(): AppDatabase {
        return database ?: synchronized(this) {
            database ?: builder.build().also { database = it }
        }
    }

    override fun close() {
        database?.close()
        database = null
    }

    override fun runAsync(runnable: Runnable) {
        executors.execute(runnable)
    }
}