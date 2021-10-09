package com.github.ykrank.androidtools.widget.uploadimg

import android.os.Parcel
import android.os.Parcelable
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import com.luck.picture.lib.entity.LocalMedia

internal class ModelImageUploadAdd : StableIdModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

class ModelImageUpload(val media: LocalMedia?) : DiffSameItem, Parcelable, StableIdModel {

    var url: String? = null
    var deleteUrl: String? = null

    var state: Int = STATE_INIT

    val path: String?
        get() = if (media == null) {
            null
        } else if (media.realPath?.endsWith(".gif") == true || !media.isCompressed) {
            media.realPath
        } else {
            media.compressPath
        }

    constructor(parcel: Parcel) : this(
        parcel.readParcelable<LocalMedia>(LocalMedia::class.java.classLoader)
    ) {
        url = parcel.readString()
        deleteUrl = parcel.readString()
        state = parcel.readInt()
    }

    override val stableId: Long
        get() = path.hashCode().toLong()

    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelImageUpload

        if (media?.path != other.media?.path) return false

        return true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(media, flags)
        parcel.writeString(url)
        parcel.writeString(deleteUrl)
        parcel.writeInt(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelImageUpload

        if (media != other.media) return false
        if (url != other.url) return false
        if (deleteUrl != other.deleteUrl) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = media.hashCode()
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (deleteUrl?.hashCode() ?: 0)
        result = 31 * result + state
        return result
    }

    override fun toString(): String {
        return "ModelImageUpload(media=$media, url=$url, deleteUrl=$deleteUrl, state=$state)"
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ModelImageUpload> {
            override fun createFromParcel(parcel: Parcel): ModelImageUpload {
                return ModelImageUpload(parcel)
            }

            override fun newArray(size: Int): Array<ModelImageUpload?> {
                return arrayOfNulls(size)
            }
        }

        const val STATE_INIT = 0
        const val STATE_UPLOADING = 1
        const val STATE_DONE = 2
        const val STATE_ERROR = 3
    }
}