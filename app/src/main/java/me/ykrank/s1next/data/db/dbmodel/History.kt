package me.ykrank.s1next.data.db.dbmodel

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * Post read history
 */
@Entity(
    tableName = "History",
    indices = [
        Index(value = ["ThreadId"], name = "IDX_History_ThreadId", unique = true),
    ]
)
class History {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * 帖子ID
     */
    @ColumnInfo(name = "ThreadId")
    var threadId = 0

    @ColumnInfo(name = "Title")
    var title: String? = null

    /**
     * 更新时间
     */
    @ColumnInfo(name = "Timestamp")
    var timestamp: Long = 0

    constructor(id: Long?, threadId: Int, title: String?, timestamp: Long) {
        this.id = id
        this.threadId = threadId
        this.title = title
        this.timestamp = timestamp
    }

    @Keep
    constructor(threadId: Int, title: String?) {
        this.threadId = threadId
        this.title = title
        timestamp = System.currentTimeMillis()
    }

    constructor()

    @Keep
    fun copyFrom(history: History) {
        threadId = history.threadId
        title = history.title
        timestamp = history.timestamp
    }
}
