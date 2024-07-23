package me.ykrank.s1next.data.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import me.ykrank.s1next.data.cache.model.CacheGroup

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

    @ColumnInfo(name = "group", defaultValue = CacheGroup.GROUP_DEFAULT)
    var group: String = CacheGroup.GROUP_DEFAULT

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

    /**
     * 解码为string后的zip blob
     */
    @Ignore
    var decodeZipString: String? = null

    constructor()
    constructor(
        key: String,
        blob: ByteArray? = null,
        title: String? = null,
        text: String? = null,
        group: String = CacheGroup.GROUP_DEFAULT,
        decodeZipString: String? = null,
    ) {
        this.key = key
        this.blob = blob
        this.title = title
        this.text = text
        this.group = group
        this.decodeZipString = decodeZipString
        this.timestamp = System.currentTimeMillis()
    }
}