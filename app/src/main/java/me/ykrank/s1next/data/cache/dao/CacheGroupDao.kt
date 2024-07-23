package me.ykrank.s1next.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.cache.dbmodel.CacheGroup


@Dao
interface CacheGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cacheGroup: CacheGroup)

    @Update
    fun update(cacheGroup: CacheGroup)

    @Query(
        "SELECT * FROM CACHEGROUP WHERE `group` == :group AND group1 == :group1 AND group2 == :group2 " +
                "AND group3==:group3 LIMIT 1"
    )
    fun query(
        group: String,
        group1: String = "",
        group2: String = "",
        group3: String = "",
    ): CacheGroup?
}