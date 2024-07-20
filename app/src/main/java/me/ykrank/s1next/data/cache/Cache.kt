package me.ykrank.s1next.data.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.ykrank.s1next.data.cache.CacheBiz

/**
 * Created by yuanke on 7/17/24
 * @author yuanke.ykrank@bytedance.com
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

    @ColumnInfo(name = "group", defaultValue = CacheBiz.DEFAULT_GROUP)
    var group: String = CacheBiz.DEFAULT_GROUP

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "zip", typeAffinity = ColumnInfo.BLOB)
    var blob: ByteArray? = null

    @ColumnInfo(name = "text")
    var text: String? = null

    /**
     * 更新时间
     */
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0

    constructor()
    constructor(
        id: Long?,
        key: String,
        title: String?,
        blob: ByteArray?,
        text: String?,
        timestamp: Long
    ) {
        this.id = id
        this.key = key
        this.title = title
        this.blob = blob
        this.text = text
        this.timestamp = timestamp
    }
}