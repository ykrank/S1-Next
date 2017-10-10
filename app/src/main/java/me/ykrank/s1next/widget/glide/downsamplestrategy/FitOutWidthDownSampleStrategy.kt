package me.ykrank.s1next.widget.glide.transformations

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * A DownsampleStrategy for Downsample Bitmap]'s size not to exceed view width size limit.
 * Created by ykrank on 2017/6/4.
 */
class FitOutWidthDownSampleStrategy : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        val fitOutSizeMultiplier: Float = requestedWidth.toFloat() / sourceWidth.toFloat()
        return if (fitOutSizeMultiplier < 1) fitOutSizeMultiplier else 1.0F
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}