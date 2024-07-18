package com.github.ykrank.androidtools.data

import java.io.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Created by yuanke on 7/16/24
 * @author yuanke.ykrank@bytedance.com
 */
data class CacheParam(
    val strategy: CacheStrategy = CacheStrategy.NET_FIRST,
    val keys: List<Serializable?> = emptyList()
) {

    constructor(
        ignoreCache: Boolean,
        keys: List<Serializable?> = emptyList(),
    ) : this(if (ignoreCache) CacheStrategy.NO_CACHE else CacheStrategy.NET_FIRST, keys)
}

data class CacheStrategy(
    // 过期策略
    val strategy: CacheStrategyData,
    // 请求失败时的降级策略
    val fallbackStrategy: CacheStrategyData,
) {
    companion object {
        val CACHE_FIRST = CacheStrategy(CacheStrategyData.ONE_DAY, CacheStrategyData.FALLBACK)
        val NET_FIRST = CacheStrategy(CacheStrategyData.NO_CACHE, CacheStrategyData.FALLBACK)
        val NO_CACHE = CacheStrategy(CacheStrategyData.NO_CACHE, CacheStrategyData.NO_CACHE)
    }
}

data class CacheStrategyData(
    val expired: Duration,
    val ignoreCache: Boolean = false,
) {

    companion object {
        val ONE_DAY = CacheStrategyData(1.toDuration(DurationUnit.DAYS))
        val FALLBACK = CacheStrategyData(Duration.INFINITE)
        val NO_CACHE = CacheStrategyData(Duration.ZERO, ignoreCache = true)
    }
}