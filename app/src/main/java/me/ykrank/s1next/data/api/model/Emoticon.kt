package me.ykrank.s1next.data.api.model

import android.util.Pair
import com.google.common.base.Objects

/**
 * @param imagePath 图片路径，不包含文件类型后缀
 */
class Emoticon(val imagePath: String, val entity: String) {

    /**
     * 文件类型，png/gif
     */
    var imageType: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Emoticon

        if (imagePath != other.imagePath) return false
        if (entity != other.entity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imagePath.hashCode()
        result = 31 * result + entity.hashCode()
        return result
    }

    companion object {
        const val TYPE_PNG = "png"
        const val TYPE_GIF = "gif"
    }
}
