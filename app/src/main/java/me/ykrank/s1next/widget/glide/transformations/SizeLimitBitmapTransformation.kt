package me.ykrank.s1next.widget.glide.transformations

import android.graphics.Bitmap

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import java.security.MessageDigest

/**
 * A [com.bumptech.glide.load.Transformation] for transforming [Bitmap]'s
 * size limit to target size.
 */
class SizeLimitBitmapTransformation(private val mSize: Int) : BitmapTransformation() {

    val id: String
        get() = "TransformationUtil.SizeMultiplierBitmapTransformation"

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val resWidth = toTransform.width
        val resHeight = toTransform.height
        if (mSize >= Math.max(resWidth, resHeight)) {
            return toTransform
        }
        val mSizeMultiplier = Math.min(mSize.toFloat() / resWidth, mSize.toFloat() / resHeight)
        return TransformationUtil.sizeMultiplier(pool, toTransform, mSizeMultiplier)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(id.toByteArray())
    }
}
