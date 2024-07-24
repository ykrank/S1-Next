package me.ykrank.s1next.data.cache.api

import androidx.annotation.WorkerThread

/**
 * Created by ykrank on 7/24/24
 */
interface ApiCacheInterceptor<T> {

    @WorkerThread
    fun interceptQueryCache(cache: T): T?

    @WorkerThread
    fun interceptSaveCache(cache: T): T?

    @WorkerThread
    fun interceptSaveKey(key: String, data: T): String = key

    /**
     * 网络数据如果无效，则降级到缓存
     */
    @WorkerThread
    fun shouldNetDataFallback(data: T): Boolean
}

/**
 * 使用统一规则校验所有数据
 */
class ApiCacheInterceptorDefault<T>(val validator: (data: T) -> Boolean) : ApiCacheInterceptor<T> {
    override fun interceptQueryCache(cache: T): T? {
        return if (validator(cache)) cache else null
    }

    override fun interceptSaveCache(cache: T): T? {
        return if (validator(cache)) cache else null
    }

    override fun shouldNetDataFallback(data: T): Boolean {
        return !validator(data)
    }

}

/**
 * 使用统一规则校验缓存数据。不校验网络数据
 */
open class ApiCacheValidatorCache<T>(val validator: (data: T) -> Boolean) : ApiCacheInterceptor<T> {
    override fun interceptQueryCache(cache: T): T? {
        return if (validator(cache)) cache else null
    }

    override fun interceptSaveCache(cache: T): T? {
        return if (validator(cache)) cache else null
    }

    override fun shouldNetDataFallback(data: T): Boolean {
        return false
    }
}

/**
 * 校验默认通过
 */
open class ApiCacheInterceptorPass<T> : ApiCacheInterceptor<T> {
    override fun interceptQueryCache(cache: T): T {
        return cache
    }

    override fun interceptSaveCache(cache: T): T {
        return cache
    }

    override fun shouldNetDataFallback(data: T): Boolean {
        return false
    }
}