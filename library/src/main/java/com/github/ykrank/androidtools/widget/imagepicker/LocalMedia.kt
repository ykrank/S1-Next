package com.github.ykrank.androidtools.widget.imagepicker

import android.net.Uri
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by yuanke on 5/27/24
 * @author yuanke.ykrank@bytedance.com
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