package me.ykrank.s1next.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import me.ykrank.s1next.data.cache.CacheConstants
import me.ykrank.s1next.data.cache.dbmodel.Cache


@Dao
interface CacheDao {
    @Query("SELECT * FROM Cache LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<Cache>

    @Query("SELECT * FROM Cache WHERE `key` == :key LIMIT 1")
    fun getByKey(key: String): Cache?

    @Query("SELECT * FROM Cache WHERE `group` == :group AND group1 == :group1 AND group2 == :group2" +
            " AND group3==:group3  ORDER BY Timestamp DESC LIMIT 1")
    fun getNewestByGroup(
        group: String,
        group1: String = CacheConstants.GROUP_EMPTY,
        group2: String = CacheConstants.GROUP_EMPTY,
        group3: String = CacheConstants.GROUP_EMPTY,
    ): Cache?

    @Query("DELETE FROM Cache WHERE _id NOT IN ( SELECT _id FROM Cache ORDER BY Timestamp DESC LIMIT :maxSize)")
    fun deleteNotTopRecords(maxSize: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cache: Cache)

    @Delete
    fun delete(cache: List<Cache>)

    @Update
    fun update(cache: Cache)

    @Query("SELECT COUNT(*) FROM Cache")
    fun getCount(): Int
}