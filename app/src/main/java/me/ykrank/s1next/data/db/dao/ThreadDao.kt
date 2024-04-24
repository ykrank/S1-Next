package me.ykrank.s1next.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.db.dbmodel.DbThread


@Dao
interface ThreadDao {
    @Query("SELECT * FROM DbThread LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<DbThread>

    @Query("SELECT * FROM DbThread WHERE ThreadId == :threadId LIMIT 1")
    fun getByThreadId(threadId: Int): DbThread?

    @Insert
    fun insert(thread: DbThread)

    @Delete
    fun delete(thread: DbThread?)

    @Update
    fun update(thread: DbThread)

    @Query("SELECT COUNT(*) FROM DbThread")
    fun getCount(): Int
}