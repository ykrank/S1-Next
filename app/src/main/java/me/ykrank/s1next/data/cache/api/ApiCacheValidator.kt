package me.ykrank.s1next.data.cache.api

/**
 * Created by ykrank on 7/24/24
 */
interface ApiCacheValidator<T> {

    fun getCacheValid(cache: T): Boolean

    fun getNetValid(cache: T): Boolean

    fun setCacheValid(cache: T): Boolean
}

/**
 * 使用统一规则校验所有数据
 */
class ApiCacheValidatorDefault<T>(val validator: (data: T) -> Boolean) : ApiCacheValidator<T> {
    override fun getCacheValid(cache: T): Boolean {
        return validator(cache)
    }

    override fun getNetValid(cache: T): Boolean {
        return validator(cache)
    }

    override fun setCacheValid(cache: T): Boolean {
        return validator(cache)
    }
}

/**
 * 使用统一规则校验缓存数据。不校验网络数据
 */
open class ApiCacheValidatorCache<T>(val validator: (data: T) -> Boolean) : ApiCacheValidator<T> {
    override fun getCacheValid(cache: T): Boolean {
        return validator(cache)
    }

    override fun getNetValid(cache: T): Boolean {
        return true
    }

    override fun setCacheValid(cache: T): Boolean {
        return validator(cache)
    }
}

/**
 * 校验默认通过
 */
open class ApiCacheValidatorPass<T> : ApiCacheValidator<T> {
    override fun getCacheValid(cache: T): Boolean {
        return true
    }

    override fun getNetValid(cache: T): Boolean {
        return true
    }

    override fun setCacheValid(cache: T): Boolean {
        return true
    }

}