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
    val isCompressed: Boolean = false,
    val compressPath: Uri? = null
) : PaperParcelable {


    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalMedia.CREATOR
    }
}