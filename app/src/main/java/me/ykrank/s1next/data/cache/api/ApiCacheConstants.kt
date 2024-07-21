package me.ykrank.s1next.data.cache.api

object ApiCacheConstants {
    object Time {
        const val TIME_LOAD_END = "load_end"
        const val TIME_LOAD_CACHE = "load_cache"
        const val TIME_SAVE_CACHE = "save_cache"
        const val TIME_PARSE_CACHE = "parse_cache"
        const val TIME_NET = "NET"
        const val TIME_PARSE_NET = "parse_net"
        const val TIME_EMIT_CACHE = "emit_cache"
        const val TIME_EMIT_NET = "emit_net"
    }

    enum class CacheType(val type: String) {
        ForumGroups("forum_groups"),
        Threads("threads"),
        Posts("posts"),
    }

}