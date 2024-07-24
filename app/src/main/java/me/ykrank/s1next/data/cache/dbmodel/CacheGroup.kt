package me.ykrank.s1next.data.cache.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.ykrank.s1next.data.cache.CacheConstants

/**
 * Created by ykrank on 7/23/24
 */
@Entity(
    tableName = "CacheGroup",
    indices = [
        Index(
            value = ["group", "group1", "group2", "group3"],
            name = "IDX_CacheGroup_Key",
            unique = true
        ),
    ]
)
class CacheGroup {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    @ColumnInfo(
        name = "group",
        defaultValue = CacheConstants.GROUP_EMPTY,
    )
    var group: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "title", defaultValue = CacheConstants.GROUP_EMPTY)
    var title: String? = null

    @ColumnInfo(name = "extra")
    var extra: String? = null

    @ColumnInfo(name = "group1", defaultValue = CacheConstants.GROUP_EMPTY)
    var group1: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "group2", defaultValue = CacheConstants.GROUP_EMPTY)
    var group2: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "group3", defaultValue = CacheConstants.GROUP_EMPTY)
    var group3: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "extra1")
    var extra1: String? = null

    @ColumnInfo(name = "extra2")
    var extra2: String? = null

    /**
     * 更新时间
     */
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0

    constructor() {
        this.timestamp = System.currentTimeMillis()
    }

    constructor(
        title: String?,
        group: String,
        group1: String? = null,
        group2: String? = null,
        group3: String? = null,
    ) {
        this.group = group
        if (group1 != null) {
            this.group1 = group1
        }
        if (group2 != null) {
            this.group2 = group2
        }
        if (group3 != null) {
            this.group3 = group3
        }
        this.title = title
        this.timestamp = System.currentTimeMillis()
    }

    fun copyFrom(other: CacheGroup) {
        title = other.title
        extra = other.extra
        extra1 = other.extra1
        extra2 = other.extra2
        timestamp = other.timestamp
    }
}