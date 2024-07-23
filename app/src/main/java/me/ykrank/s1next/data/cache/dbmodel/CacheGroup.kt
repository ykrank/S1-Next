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
        defaultValue = CacheConstants.CacheGroup.GROUP_DEFAULT,
    )
    var group: String = CacheConstants.CacheGroup.GROUP_DEFAULT

    @ColumnInfo(name = "group1", defaultValue = "")
    var group1: String = ""

    @ColumnInfo(name = "group2", defaultValue = "")
    var group2: String = ""

    @ColumnInfo(name = "group3", defaultValue = "")
    var group3: String = ""

    @ColumnInfo(name = "title", defaultValue = "")
    var title: String? = null

    constructor()

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
    }
}