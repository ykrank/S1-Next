package me.ykrank.s1next.data.db.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.db.dbmodel.BlackWord


@Dao
interface BlackWordDao {
    @Query("SELECT * FROM BlackWord LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<BlackWord>

    @Query("SELECT * FROM BlackWord")
    fun loadCursor(): Cursor

    @Query("SELECT * FROM BlackWord WHERE Stat != ${BlackWord.NORMAL}")
    fun loadNotNormal(): List<BlackWord>

    @Query("SELECT * FROM BlackWord WHERE Word == :word LIMIT 1")
    fun getByWord(word: String): BlackWord?

    @Insert
    fun insert(blackWord: BlackWord)

    @Delete
    fun delete(blackWord: List<BlackWord>)

    @Update
    fun update(blackWord: BlackWord)

    @Query("SELECT COUNT(*) FROM BlackWord")
    fun getCount(): Int
}