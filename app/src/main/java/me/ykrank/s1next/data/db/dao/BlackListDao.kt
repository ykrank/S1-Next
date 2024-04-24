package me.ykrank.s1next.data.db.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.db.dbmodel.BlackList


@Dao
interface BlackListDao {
    @Query("SELECT * FROM BlackList LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<BlackList>

    @Query("SELECT * FROM BlackList")
    fun loadCursor(): Cursor

    @Query("SELECT * FROM BlackList WHERE AuthorId = :authorId")
    fun getByAuthorId(authorId: Int): List<BlackList>

    @Query("SELECT * FROM BlackList WHERE Author = :author")
    fun getByAuthor(author: String): List<BlackList>

    @Query("SELECT * FROM BlackList WHERE Author = :author AND AuthorId = :authorId")
    fun getByAuthorAndId(authorId: Int, author: String): List<BlackList>

    @Insert
    fun insert(blackList: List<BlackList>)

    @Delete
    fun delete(blackList: List<BlackList>)

    @Update
    fun update(blackList: List<BlackList>)
}