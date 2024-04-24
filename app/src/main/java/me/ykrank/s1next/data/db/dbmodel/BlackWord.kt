package me.ykrank.s1next.data.db.dbmodel

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.ykrank.s1next.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "BlackWord",
    indices = [
        Index(value = ["Word"], name = "IDX_BlackWord_Word", unique = true),
    ]
)
class BlackWord : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * 屏蔽词
     */
    @ColumnInfo(name = "Word")
    var word: String? = null

    /**
     * 屏蔽状态
     */
    @ColumnInfo(name = "Stat")
    @BlackWordFLag
    var stat = NORMAL

    /**
     * 屏蔽时的时间
     */
    @ColumnInfo(name = "Timestamp")
    var timestamp: Long

    /**
     * 是否已同步
     */
    @ColumnInfo(name = "Upload")
    var isUpload = false

    constructor() {
        timestamp = System.currentTimeMillis()
    }

    constructor(word: String?, stat: Int) {
        this.word = word
        this.stat = stat
        timestamp = System.currentTimeMillis()
        isUpload = false
    }

    protected constructor(`in`: Parcel) {
        id = if (`in`.readByte().toInt() == 0) {
            null
        } else {
            `in`.readLong()
        }
        word = `in`.readString()
        stat = `in`.readInt()
        timestamp = `in`.readLong()
        isUpload = `in`.readByte().toInt() != 0
    }

    constructor(id: Long?, word: String?, stat: Int, timestamp: Long, upload: Boolean) {
        this.id = id
        this.word = word
        this.stat = stat
        this.timestamp = timestamp
        isUpload = upload
    }

    @get:StringRes
    val statRes: Int
        get() = when (stat) {
            HIDE -> R.string.blacklist_flag_hide
            DEL -> R.string.blacklist_flag_del
            else -> R.string.blacklist_flag_normal
        }
    val time: String
        get() {
            val sdf = SimpleDateFormat(TimeFormat, Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    override fun toString(): String {
        return "BlackWord{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", stat=" + stat +
                ", timestamp=" + timestamp +
                ", upload=" + isUpload +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val blackWord = other as BlackWord
        if (stat != blackWord.stat) return false
        if (timestamp != blackWord.timestamp) return false
        if (isUpload != blackWord.isUpload) return false
        if (if (id != null) id != blackWord.id else blackWord.id != null) return false
        return if (word != null) word == blackWord.word else blackWord.word == null
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (word != null) word.hashCode() else 0
        result = 31 * result + stat
        result = 31 * result + (timestamp xor (timestamp ushr 32)).toInt()
        result = 31 * result + if (isUpload) 1 else 0
        return result
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
        dest.writeString(word)
        dest.writeInt(stat)
        dest.writeLong(timestamp)
        dest.writeByte((if (isUpload) 1 else 0).toByte())
    }

    fun mergeFrom(blackWord: BlackWord) {
        word = blackWord.word
        stat = blackWord.stat
        timestamp = blackWord.timestamp
    }

    @IntDef(NORMAL, HIDE, DEL)
    annotation class BlackWordFLag
    companion object {
        const val NORMAL = 0
        const val HIDE = 1
        const val DEL = 2
        private const val TimeFormat = "yyyy-MM-dd HH:mm"
        @JvmField
        val CREATOR: Parcelable.Creator<BlackWord?> = object : Parcelable.Creator<BlackWord?> {
            override fun createFromParcel(`in`: Parcel): BlackWord {
                return BlackWord(`in`)
            }

            override fun newArray(size: Int): Array<BlackWord?> {
                return arrayOfNulls(size)
            }
        }
    }
}
