package me.ykrank.s1next.widget.download

/**
 * Created by yuanke on 7/12/24
 * @author yuanke.ykrank@bytedance.com
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