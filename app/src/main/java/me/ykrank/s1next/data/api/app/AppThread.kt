package me.ykrank.s1next.data.api.app

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.ykrank.s1next.data.SameItem
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by ykrank on 2017/7/22.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@PaperParcel
class AppThread() : PaperParcelable, SameItem {
    @JsonProperty("tid")
    var tid: Int = 0
    @JsonProperty("fid")
    var fid: Int = 0
    @JsonProperty("typeid")
    var typeId: Int = 0
    @JsonProperty("author")
    var author: String? = null
    @JsonProperty("authorid")
    var authorid: Int = 0
    @JsonProperty("subject")
    var subject: String? = null
    @JsonProperty("dateline")
    var dateline: Int = 0
    @JsonProperty("lastpost")
    var lastPost: Int = 0
    @JsonProperty("views")
    var views: Int = 0
    @JsonProperty("replies")
    var replies: Int = 0
    @JsonProperty("special")
    var special: String? = null
    @JsonProperty("type")
    var type: String? = null
    @JsonProperty("statusicon")
    var statusIcon: String? = null
    @JsonProperty("pid")
    var pid: Int = 0
    @JsonProperty("fname")
    var fName: String? = null
    @JsonProperty("favorite")
    var favorite: Boolean = false

    constructor(parcel: Parcel) : this() {
        tid = parcel.readInt()
        fid = parcel.readInt()
        typeId = parcel.readInt()
        author = parcel.readString()
        authorid = parcel.readInt()
        subject = parcel.readString()
        dateline = parcel.readInt()
        lastPost = parcel.readInt()
        views = parcel.readInt()
        replies = parcel.readInt()
        special = parcel.readString()
        type = parcel.readString()
        statusIcon = parcel.readString()
        pid = parcel.readInt()
        fName = parcel.readString()
        favorite = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(tid)
        dest.writeInt(fid)
        dest.writeInt(typeId)
        dest.writeString(author)
        dest.writeInt(authorid)
        dest.writeString(subject)
        dest.writeInt(dateline)
        dest.writeInt(lastPost)
        dest.writeInt(views)
        dest.writeInt(replies)
        dest.writeString(special)
        dest.writeString(type)
        dest.writeString(statusIcon)
        dest.writeInt(pid)
        dest.writeString(fName)
        dest.writeByte(if (favorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AppThread

        if (tid != other.tid) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AppThread

        if (tid != other.tid) return false
        if (fid != other.fid) return false
        if (typeId != other.typeId) return false
        if (author != other.author) return false
        if (authorid != other.authorid) return false
        if (subject != other.subject) return false
        if (dateline != other.dateline) return false
        if (lastPost != other.lastPost) return false
        if (views != other.views) return false
        if (replies != other.replies) return false
        if (special != other.special) return false
        if (type != other.type) return false
        if (statusIcon != other.statusIcon) return false
        if (pid != other.pid) return false
        if (fName != other.fName) return false
        if (favorite != other.favorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tid
        result = 31 * result + fid
        result = 31 * result + typeId
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + authorid
        result = 31 * result + (subject?.hashCode() ?: 0)
        result = 31 * result + dateline
        result = 31 * result + lastPost
        result = 31 * result + views
        result = 31 * result + replies
        result = 31 * result + (special?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (statusIcon?.hashCode() ?: 0)
        result = 31 * result + pid
        result = 31 * result + (fName?.hashCode() ?: 0)
        result = 31 * result + favorite.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<AppThread> {
        override fun createFromParcel(parcel: Parcel): AppThread {
            return AppThread(parcel)
        }

        override fun newArray(size: Int): Array<AppThread?> {
            return arrayOfNulls(size)
        }
    }


}
