package me.ykrank.s1next.data.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface CacheDao {
    @Query("SELECT * FROM Cache LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<Cache>

    @Query("SELECT * FROM Cache WHERE `key` == :key LIMIT 1")
    fun getByKey(key: String): Cache?

    @Query("DELETE FROM Cache WHERE `group` == :group AND _id NOT IN ( SELECT _id FROM Cache WHERE `group` == :group ORDER BY Timestamp DESC LIMIT :maxSize)")
    fun deleteNotTopRecords(group: String, maxSize: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cache: Cache)

    @Delete
    fun delete(cache: List<Cache>)

    @Update
    fun update(cache: Cache)

    @Query("SELECT COUNT(*) FROM Cache")
    fun getCount(): Int
}