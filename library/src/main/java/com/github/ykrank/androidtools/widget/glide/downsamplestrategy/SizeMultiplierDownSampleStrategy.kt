package com.github.ykrank.androidtools.widget.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * A DownsampleStrategy for Downsample Bitmap]'s size with a multiplier.
 * Gif is invalid.
 * Created by ykrank on 2017/6/4.
 */
class SizeMultiplierDownSampleStrategy(val sizeMultiplier: Float) : DownsampleStrategy() {
    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        return sizeMultiplier
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }

}