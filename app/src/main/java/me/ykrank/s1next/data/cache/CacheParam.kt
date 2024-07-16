package me.ykrank.s1next.data.cache

import java.io.Serializable

/**
 * Created by yuanke on 7/16/24
 * @author yuanke.ykrank@bytedance.com
 */
data class CacheParam(
    val ignoreCache: Boolean = false,
    val keys: List<Serializable?> = emptyList()
) {

    companion object {
        val EMPTY = CacheParam()
    }
}
