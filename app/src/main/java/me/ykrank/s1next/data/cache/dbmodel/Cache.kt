package me.ykrank.s1next.data.cache.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import me.ykrank.s1next.data.cache.CacheConstants
import me.ykrank.s1next.data.cache.exmodel.CacheGroupModel

/**
 * Created by ykrank on 7/17/24
 * 
 */
@Entity(
    tableName = "Cache",
    indices = [
        Index(value = ["key"], name = "IDX_Cache_Key", unique = true),
    ]
)
class Cache {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * 唯一key
     */
    @ColumnInfo(name = "key")
    var key: String = ""

    @ColumnInfo(name = "uid")
    var uid: Int? = null

    @ColumnInfo(name = "group", defaultValue = CacheConstants.GROUP_EMPTY)
    var group: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "zip", typeAffinity = ColumnInfo.BLOB)
    var blob: ByteArray? = null

    @ColumnInfo(name = "text")
    var text: String? = null

    @ColumnInfo(name = "group1", defaultValue = CacheConstants.GROUP_EMPTY)
    var group1: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "group2", defaultValue = CacheConstants.GROUP_EMPTY)
    var group2: String = CacheConstants.GROUP_EMPTY

    @ColumnInfo(name = "group3", defaultValue = CacheConstants.GROUP_EMPTY)
    var group3: String = CacheConstants.GROUP_EMPTY

    /**
     * 更新时间
     */
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0

    /**
     * 解码为string后的zip blob
     */
    @Ignore
    var decodeZipString: String? = null

    constructor()
    constructor(
        key: String,
        uid: Int?,
        blob: ByteArray? = null,
        title: String? = null,
        text: String? = null,
        groups: List<String> = emptyList(),
        decodeZipString: String? = null,
    ) {
        this.key = key
        this.uid = uid
        this.blob = blob
        this.title = title
        this.text = text
        this.decodeZipString = decodeZipString
        this.timestamp = System.currentTimeMillis()
        val groupModel = CacheGroupModel(groups)
        this.group = groupModel.group
        this.group1 = groupModel.group1
        this.group2 = groupModel.group2
        this.group3 = groupModel.group3
    }
}