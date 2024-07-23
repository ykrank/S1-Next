package me.ykrank.s1next.util

import org.jsoup.nodes.Entities

/**
 * Created by ykrank on 6/20/24
 * 
 */
object HtmlUtils {

    fun unescapeHtml(string: String?): String? {
        if (string == null) return null
        return Entities.unescape(string)
    }
}