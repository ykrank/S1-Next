package me.ykrank.s1next.data.db.dbmodel

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
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

/**
 * 黑名单数据库
 * 使用了ActiveAndroid
 * Created by AdminYkrank on 2016/2/23.
 */
@Entity(
    tableName = "BlackList",
    indices = [
        Index(value = ["AuthorId"], name = "IDX_BlackList_AuthorId"),
        Index(value = ["Author"], name = "IDX_BlackList_Author"),
    ]
)
class BlackList : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    /**
     * Id
     */
    @ColumnInfo(name = "AuthorId")
    var authorId = 0

    /**
     * 用户名
     */
    @ColumnInfo(name = "Author")
    var author: String? = null

    /**
     * 回复的屏蔽状态
     */
    @get:PostFLag
    @ColumnInfo(name = "Post")
    @PostFLag
    var post = NORMAL

    /**
     * 主题的屏蔽状态
     */
    @get:ForumFLag
    @ColumnInfo(name = "Forum")
    @ForumFLag
    var forum = NORMAL

    /**
     * 屏蔽时的备注
     */
    @ColumnInfo(name = "Remark")
    var remark: String? = null

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

    constructor(`in`: Parcel) {
        val hasId = `in`.readByte().toInt() == 1
        if (hasId) {
            id = `in`.readLong()
        }
        authorId = `in`.readInt()
        author = `in`.readString()
        post = `in`.readInt()
        forum = `in`.readInt()
        remark = `in`.readString()
        timestamp = `in`.readLong()
        isUpload = `in`.readByte().toInt() != 0
    }

    constructor() {
        timestamp = System.currentTimeMillis()
    }

    constructor(authorId: Int, name: String?, @PostFLag post: Int, @ForumFLag forum: Int) {
        this.authorId = authorId
        author = name
        this.post = post
        this.forum = forum
        remark = ""
        timestamp = System.currentTimeMillis()
        isUpload = false
    }

    constructor(
        id: Long?, authorId: Int, author: String?, post: Int, forum: Int, remark: String?,
        timestamp: Long, upload: Boolean
    ) {
        this.id = id
        this.authorId = authorId
        this.author = author
        this.post = post
        this.forum = forum
        this.remark = remark
        this.timestamp = timestamp
        isUpload = upload
    }

    fun mergeFrom(bList: BlackList) {
        if (bList.authorId > 0) {
            authorId = bList.authorId
        }
        if (!bList.author.isNullOrEmpty()) {
            author = bList.author
        }
        post = bList.post
        forum = bList.forum
        if (!bList.remark.isNullOrEmpty()) {
            remark = bList.remark
        }
        timestamp = bList.timestamp
        isUpload = bList.isUpload
    }

    @get:StringRes
    val postRes: Int
        get() = when (post) {
            HIDE_POST -> R.string.blacklist_flag_hide
            DEL_POST -> R.string.blacklist_flag_del
            else -> R.string.blacklist_flag_normal
        }

    @get:StringRes
    val forumRes: Int
        get() = when (forum) {
            HIDE_FORUM -> R.string.blacklist_flag_hide
            DEL_FORUM -> R.string.blacklist_flag_del
            else -> R.string.blacklist_flag_normal
        }
    val isForumHide: Boolean
        get() = forum == HIDE_FORUM
    val isPostHide: Boolean
        get() = post == HIDE_POST
    val time: String
        get() {
            val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
            return sdf.format(Date(timestamp))
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
        dest.writeInt(authorId)
        dest.writeString(author)
        dest.writeInt(post)
        dest.writeInt(forum)
        dest.writeString(remark)
        dest.writeLong(timestamp)
        dest.writeByte((if (isUpload) 1 else 0).toByte())
    }

    override fun toString(): String {
        return "BlackList{" +
                "authorId=" + authorId +
                ", author='" + author + '\'' +
                ", post=" + post +
                ", forum=" + forum +
                ", remark='" + remark + '\'' +
                ", timestamp=" + time +
                ", upload=" + isUpload +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val blackList = other as BlackList
        if (authorId != blackList.authorId) return false
        if (post != blackList.post) return false
        if (forum != blackList.forum) return false
        if (timestamp != blackList.timestamp) return false
        if (isUpload != blackList.isUpload) return false
        if (if (id != null) id != blackList.id else blackList.id != null) return false
        if (if (author != null) author != blackList.author else blackList.author != null) return false
        return if (remark != null) remark == blackList.remark else blackList.remark == null
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + authorId
        result = 31 * result + if (author != null) author.hashCode() else 0
        result = 31 * result + post
        result = 31 * result + forum
        result = 31 * result + if (remark != null) remark.hashCode() else 0
        result = 31 * result + (timestamp xor (timestamp ushr 32)).toInt()
        result = 31 * result + if (isUpload) 1 else 0
        return result
    }

    @IntDef(NORMAL, HIDE_POST, DEL_POST)
    annotation class PostFLag

    @IntDef(NORMAL, HIDE_FORUM, DEL_FORUM)
    annotation class ForumFLag
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BlackList?> = object : Parcelable.Creator<BlackList?> {
            override fun createFromParcel(`in`: Parcel): BlackList {
                return BlackList(`in`)
            }

            override fun newArray(size: Int): Array<BlackList?> {
                return arrayOfNulls(size)
            }
        }
        const val NORMAL = 0
        const val HIDE_POST = 1
        const val DEL_POST = 2
        const val HIDE_FORUM = 3
        const val DEL_FORUM = 4
        const val timeFormat = "yyyy-MM-dd HH:mm"
    }
}