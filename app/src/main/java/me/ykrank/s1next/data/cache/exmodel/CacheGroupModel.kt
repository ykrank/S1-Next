package me.ykrank.s1next.data.cache.exmodel

import me.ykrank.s1next.data.cache.CacheConstants

/**
 * Created by ykrank on 7/24/24
 */
data class CacheGroupModel(
    val group: String = CacheConstants.GROUP_EMPTY,
    val group1: String = CacheConstants.GROUP_EMPTY,
    val group2: String = CacheConstants.GROUP_EMPTY,
    val group3: String = CacheConstants.GROUP_EMPTY,
) {

    constructor(groups: List<String>) : this(
        groups.getOrElse(0) {
            CacheConstants.GROUP_EMPTY
        },
        groups.getOrElse(1) {
            CacheConstants.GROUP_EMPTY
        },
        groups.getOrElse(2) {
            CacheConstants.GROUP_EMPTY
        },
        groups.getOrElse(3) {
            CacheConstants.GROUP_EMPTY
        }
    )
}

data class CacheGroupExtra(
    val extra: String? = null,
    val extra1: String? = null,
    val extra2: String? = null,
) {

    constructor(extras: List<String>) : this(
        extras.getOrNull(0),
        extras.getOrNull(1),
        extras.getOrNull(2),
    )
}