package com.github.ykrank.androidtools.data

/**
 * Created by yuanke on 7/16/24
 * @author yuanke.ykrank@bytedance.com
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