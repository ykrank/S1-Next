package com.github.ykrank.androidtools.widget.imagepicker

import android.net.Uri
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by ykrank on 5/27/24
 * 
 */
@PaperParcel
data class LocalMedia(
    val uri: Uri,
) : PaperParcelable {

    @Transient
    val isCompressed = false

    @Transient
    val compressPath: String? = null

    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalMedia.CREATOR
    }
}