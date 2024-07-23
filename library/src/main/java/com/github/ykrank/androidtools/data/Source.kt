package com.github.ykrank.androidtools.data

/**
 * Created by ykrank on 7/16/24
 * 
 */
enum class Source {
    MEMORY, PERSISTENCE, CLOUD;

    fun isCache(): Boolean {
        return this == MEMORY || this == PERSISTENCE
    }

    fun isCloud(): Boolean {
        return this == CLOUD
    }
}