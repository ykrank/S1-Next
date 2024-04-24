package me.ykrank.s1next.data.db.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.db.biz.ReadProgressBiz
import me.ykrank.s1next.data.db.dbmodel.ReadProgress


@Dao
interface ReadProgressDao {
    @Query("SELECT * FROM ReadProgress LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<ReadProgress>

    @Query("SELECT * FROM ReadProgress WHERE ThreadId == :threadId LIMIT 1")
    fun getByThreadId(threadId: Int): ReadProgress?

    @Insert
    fun insert(readProgress: ReadProgress)

    @Delete
    fun delete(readProgress: ReadProgress)

    @Update
    fun update(readProgress: ReadProgress)

    @Query("SELECT COUNT(*) FROM ReadProgress")
    fun getCount(): Int
}