package com.github.ykrank.androidtools.data

/**
 * Created by ykrank on 7/16/24
 *
 */
enum class Source {
    // 短期内缓存，可视为和网络数据一致
    MEMORY,

    // 较长期缓存
    PERSISTENCE,

    // 网络最新数据
    CLOUD;

    fun isCache(): Boolean {
        return this == MEMORY || this == PERSISTENCE
    }

    fun isCloud(): Boolean {
        return this == CLOUD
    }

    fun isNewData(): Boolean {
        return this == CLOUD || this == MEMORY
    }

    fun isDisk(): Boolean {
        return this == PERSISTENCE
    }
}