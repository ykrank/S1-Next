package me.ykrank.s1next.data.db.dbmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by yuanke on 7/17/24
 * @author yuanke.ykrank@bytedance.com
 */
@Entity(
    tableName = "Cache",
)
class Cache {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * 唯一key
     */
    @ColumnInfo(name = "key", index = true)
    var key: String = ""

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