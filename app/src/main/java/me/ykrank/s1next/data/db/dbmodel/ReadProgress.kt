package me.ykrank.s1next.data.db.dbmodel

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.common.base.Objects
import paperparcel.PaperParcel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by AdminYkrank on 2016/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@Entity(
    tableName = "ReadProgress",
    indices = [
        Index(value = ["ThreadId"], name = "IDX_ReadProgress_ThreadId", unique = true),
    ]
)
@PaperParcel
class ReadProgress : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * 帖子ID
     */
    @ColumnInfo(name = "ThreadId")
    var threadId = 0

    /**
     * 页数
     */
    @ColumnInfo(name = "Page")
    var page = 0

    /**
     * 位置
     */
    @ColumnInfo(name = "Position")
    var position = 0

    /**
     * Item 对应View和上边界的偏移
     */
    @ColumnInfo(name = "Offset")
    var offset = 0

    /**
     * 更新时间
     */
    @ColumnInfo(name = "Timestamp")
    var timestamp: Long

    constructor() {
        timestamp = System.currentTimeMillis()
    }

    constructor(threadId: Int, page: Int, position: Int, offset: Int) : super() {
        this.threadId = threadId
        this.page = page
        this.position = position
        this.offset = offset
        timestamp = System.currentTimeMillis()
    }

    constructor(
        id: Long?, threadId: Int, page: Int, position: Int, offset: Int,
        timestamp: Long
    ) {
        this.id = id
        this.threadId = threadId
        this.page = page
        this.position = position
        this.offset = offset
        this.timestamp = timestamp
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelReadProgress.writeToParcel(this, dest, flags)
    }

    val time: String
        get() {
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    fun copyFrom(oReadProgress: ReadProgress) {
        threadId = oReadProgress.threadId
        page = oReadProgress.page
        position = oReadProgress.position
        offset = oReadProgress.offset
        timestamp = oReadProgress.timestamp
    }

    override fun toString(): String {
        return "ReadProgress{" +
                "id=" + id +
                ", threadId=" + threadId +
                ", page=" + page +
                ", position=" + position +
                ", offset=" + offset +
                ", timestamp=" + timestamp +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ReadProgress
        return threadId == that.threadId && page == that.page && position == that.position && offset == that.offset && timestamp == that.timestamp &&
                Objects.equal(id, that.id)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, threadId, page, position, offset, timestamp)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReadProgress> = PaperParcelReadProgress.CREATOR
        const val TIME_FORMAT = "yyyy-MM-dd HH:mm"
    }
}
