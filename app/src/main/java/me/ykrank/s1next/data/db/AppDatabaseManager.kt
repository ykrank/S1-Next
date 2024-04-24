package me.ykrank.s1next.data.db

import android.content.Context
import androidx.room.Room
import me.ykrank.s1next.BuildConfig

interface AppDatabaseManager {
    fun getOrBuildDb(): AppDatabase

    fun close()
}

class AppDatabaseManagerImpl(applicationContext: Context) : AppDatabaseManager {
    val builder = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, BuildConfig.DB_NAME
    )

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

}