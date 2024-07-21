package me.ykrank.s1next.data.cache.model

data class BaseCache<T>(val time: Long, val data: T) {
}