package me.ykrank.s1next.widget.download

/**
 * Created by ykrank on 7/12/24
 * 
 */
class DownloadProgressModel(
    val totalLength: Long,
    val currentOffset: Long,
    val percentIsAvailable: Boolean,
    val done: Boolean,
) {

    constructor(totalLength: Long, currentOffset: Long, done: Boolean) :
            this(
                totalLength = totalLength,
                currentOffset = currentOffset,
                percentIsAvailable = totalLength > 0,
                done = done
            )

    val progress by lazy {
        if (percentIsAvailable) (currentOffset * 100f / totalLength).toInt() else 0
    }
}