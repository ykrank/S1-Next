package me.ykrank.s1next.data.db.dbmodel

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.common.base.Objects

/**
 * Created by ykrank on 2017/1/23.
 * 帖子相关信息数据库
 */
@Entity(
    tableName = "DbThread",
    indices = [
        Index(value = ["ThreadId"], name = "IDX_DbThread_ThreadId", unique = true),
    ]
)
class DbThread : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * 帖子ID
     */
    @ColumnInfo(name = "ThreadId")
    var threadId = 0

    /**
     * 最后一次访问时的回复数
     */
    @ColumnInfo(name = "LastCountWhenView")
    var lastCountWhenView = 0

    /**
     * 更新时间
     */
    @ColumnInfo(name = "Timestamp")
    var timestamp: Long

    constructor(id: Long?, threadId: Int, lastCountWhenView: Int, timestamp: Long) {
        this.id = id
        this.threadId = threadId
        this.lastCountWhenView = lastCountWhenView
        this.timestamp = timestamp
    }

    constructor(threadId: Int, lastCountWhenView: Int) {
        this.threadId = threadId
        this.lastCountWhenView = lastCountWhenView
        timestamp = System.currentTimeMillis()
    }

    constructor() {
        timestamp = System.currentTimeMillis()
    }

    protected constructor(`in`: Parcel) {
        val hasId = `in`.readByte().toInt() == 1
        if (hasId) {
            id = `in`.readLong()
        }
        threadId = `in`.readInt()
        lastCountWhenView = `in`.readInt()
        timestamp = `in`.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        id.apply {
            if (this == null) {
                dest.writeByte(0.toByte())
            } else {
                dest.writeByte(1.toByte())
                dest.writeLong(this)
            }
        }
        dest.writeInt(threadId)
        dest.writeInt(lastCountWhenView)
        dest.writeLong(timestamp)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val dbThread = other as DbThread
        return lastCountWhenView == dbThread.lastCountWhenView && timestamp == dbThread.timestamp &&
                Objects.equal(id, dbThread.id) &&
                Objects.equal(threadId, dbThread.threadId)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, threadId, lastCountWhenView, timestamp)
    }

    fun copyFrom(oDbThread: DbThread) {
        threadId = oDbThread.threadId
        lastCountWhenView = oDbThread.lastCountWhenView
        timestamp = oDbThread.timestamp
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DbThread?> = object : Parcelable.Creator<DbThread?> {
            override fun createFromParcel(`in`: Parcel): DbThread {
                return DbThread(`in`)
            }

            override fun newArray(size: Int): Array<DbThread?> {
                return arrayOfNulls(size)
            }
        }
    }
}
