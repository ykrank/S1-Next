package com.github.ykrank.androidtools.data

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Created by ykrank on 7/16/24
 * 
 */
data class CacheParam(
    val strategy: CacheStrategy = CacheStrategy.NET_FIRST,
) {

    constructor(
        ignoreCache: Boolean,
    ) : this(if (ignoreCache) CacheStrategy.NO_CACHE else CacheStrategy.NET_FIRST)
}

data class CacheStrategy(
    // 过期策略
    val strategy: CacheStrategyData,
    // 请求失败时的降级策略
    val fallbackStrategy: CacheStrategyData,
    // 超时则先降级返回缓存
    val fallbackTimeout: Duration = 3.toDuration(DurationUnit.SECONDS),
) {
    companion object {
        val CACHE_FIRST = CacheStrategy(CacheStrategyData.ONE_DAY, CacheStrategyData.INFINITE)
        val NET_FIRST = CacheStrategy(CacheStrategyData.NO_CACHE, CacheStrategyData.INFINITE)
        val NO_CACHE = CacheStrategy(CacheStrategyData.NO_CACHE, CacheStrategyData.NO_CACHE)
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