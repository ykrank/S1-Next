package me.ykrank.s1next.widget.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * A DownsampleStrategy for Downsample Bitmap]'s size with target size.Scale type use CENTER_INSIDE
 * Created by ykrank on 2017/6/4.
 */
class SizeDownSampleStrategy(val size: Int) : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        val sizeMultiplier: Float = Math.min(size.toFloat() / sourceWidth, size.toFloat() / sourceHeight)
        return if (sizeMultiplier < 1) sizeMultiplier else 1.0F
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}