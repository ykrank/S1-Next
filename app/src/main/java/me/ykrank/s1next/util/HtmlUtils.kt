package me.ykrank.s1next.util

import org.jsoup.nodes.Entities

/**
 * Created by yuanke on 6/20/24
 * @author yuanke.ykrank@bytedance.com
 */
object HtmlUtils {

    fun unescapeHtml(string: String?): String? {
        if (string == null) return null
        return Entities.unescape(string)
    }
}