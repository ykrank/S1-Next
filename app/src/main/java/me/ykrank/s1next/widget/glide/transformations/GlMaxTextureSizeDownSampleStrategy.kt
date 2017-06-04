package me.ykrank.s1next.widget.glide.transformations

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * A DownsampleStrategy for Downsample Bitmap]'s size not to exceed the OpenGl texture size limit.
 * Created by ykrank on 2017/6/4.
 */
class GlMaxTextureSizeDownSampleStrategy : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        val maxTextureSize: Float = GlMaxTextureCalculator.getInstance().glMaxTextureSize.toFloat()
        val sizeMultiplier: Float = Math.min(maxTextureSize / sourceWidth, maxTextureSize / sourceHeight)
        return if (sizeMultiplier < 1) sizeMultiplier else 1.0F
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}