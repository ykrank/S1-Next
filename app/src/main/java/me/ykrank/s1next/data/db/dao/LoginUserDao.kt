package me.ykrank.s1next.data.db.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.db.biz.HistoryBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.data.db.dbmodel.LoginUser


@Dao
interface LoginUserDao {
    @Query("SELECT * FROM LoginUser LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<LoginUser>

    @Query("SELECT * FROM LoginUser")
    fun loadAll(): List<LoginUser>

    @Query("SELECT * FROM LoginUser WHERE uid = :uid LIMIT 1")
    fun getByUid(uid: Int): LoginUser?

    @Insert
    fun insert(loginUser: LoginUser)

    @Delete
    fun delete(loginUser: List<LoginUser>)

    @Update
    fun update(loginUser: LoginUser)

    @Query("SELECT COUNT(*) FROM LoginUser")
    fun getCount(): Int
}