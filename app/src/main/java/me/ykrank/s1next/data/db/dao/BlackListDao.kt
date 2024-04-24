package me.ykrank.s1next.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import me.ykrank.s1next.data.db.dbmodel.BlackList

@Dao
interface BlackListDao {
    @Query("SELECT * FROM BlackList LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<BlackList>
}