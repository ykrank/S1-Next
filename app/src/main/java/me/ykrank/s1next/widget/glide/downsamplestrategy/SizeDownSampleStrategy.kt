package me.ykrank.s1next.widget.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * A DownsampleStrategy for Downsample Bitmap]'s size with target size.Scale type use CENTER_INSIDE
 * Created by ykrank on 2017/6/4.
 */
class SizeDownSampleStrategy(val size: Float) : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        return Math.min(size / sourceWidth, size / sourceHeight);
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}