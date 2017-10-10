package me.ykrank.s1next.widget.glide.downsamplestrategy

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

/**
 * A DownsampleStrategy for choose min scale.
 * Created by ykrank on 2017/6/4.
 */
class MultiDownSampleStrategy(vararg strategy: DownsampleStrategy) : DownsampleStrategy() {

    val strateies: List<DownsampleStrategy> = strategy.toList()

    override fun getScaleFactor(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): Float {
        return strateies.map { it.getScaleFactor(sourceWidth, sourceHeight, requestedWidth, requestedHeight) }
                .sorted()
                .first()
    }

    override fun getSampleSizeRounding(sourceWidth: Int, sourceHeight: Int, requestedWidth: Int, requestedHeight: Int): SampleSizeRounding {
        return if (strateies.any { it.getSampleSizeRounding(sourceWidth, sourceHeight, requestedWidth, requestedHeight) == SampleSizeRounding.QUALITY })
            SampleSizeRounding.QUALITY
        else SampleSizeRounding.MEMORY
    }

}