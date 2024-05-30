package com.github.ykrank.androidtools.widget.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import kotlin.math.min

/**
 * A DownsampleStrategy for Downsample Bitmap]'s size with target size.Scale type use CENTER_INSIDE
 * Gif is invalid.
 * Created by ykrank on 2017/6/4.
 */
class SizeDownSampleStrategy(val size: Int) : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        return min(size.toFloat() / sourceWidth, size.toFloat() / sourceHeight)
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}