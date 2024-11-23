package com.github.ykrank.androidtools.data

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Created by ykrank on 7/16/24
 *
 */
data class CacheParam(
    val strategy: CacheStrategy = CacheStrategy.DEFAULT,
) {

    constructor(
        ignoreCache: Boolean,
    ) : this(if (ignoreCache) CacheStrategy.NO_CACHE else CacheStrategy.DEFAULT)
}

data class CacheStrategy(
    // 过期策略
    val strategy: CacheStrategyData,
    // 请求失败时的降级策略
    val fallbackStrategy: CacheStrategyData,
    // 超时则先降级返回缓存
    val fallbackTimeout: Duration = (2500).toDuration(DurationUnit.MILLISECONDS),
) {
    companion object {
        // 10分钟内优先缓存，其次网络，短时间超时或失败时降级到缓存
        val DEFAULT = CacheStrategy(CacheStrategyData(10.toDuration(DurationUnit.MINUTES)), CacheStrategyData.INFINITE)
        val CACHE_FIRST = CacheStrategy(CacheStrategyData.ONE_DAY, CacheStrategyData.INFINITE)
        val NET_FIRST = CacheStrategy(CacheStrategyData.NO_CACHE, CacheStrategyData.INFINITE)
        val NO_CACHE = CacheStrategy(CacheStrategyData.NO_CACHE, CacheStrategyData.NO_CACHE, Duration.INFINITE)
    }
}

data class CacheStrategyData(
    val expired: Duration,
    val ignoreCache: Boolean = false,
) {

    companion object {
        val ONE_DAY = CacheStrategyData(1.toDuration(DurationUnit.DAYS))
        val INFINITE = CacheStrategyData(Duration.INFINITE)
        val NO_CACHE = CacheStrategyData(Duration.ZERO, ignoreCache = true)
    }
}