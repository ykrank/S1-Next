package me.ykrank.s1next.data.api.model

import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.model.wrapper.HomeReplyWebWrapper
import org.jsoup.nodes.Element

/**
 * Created by ykrank on 2017/2/5.
 * User's reply model
 */

class HomeReply : DiffSameItem, HomeReplyWebWrapper.HomeReplyItem, StableIdModel {
    var reply: String? = null
    //eg forum.php?mod=redirect&goto=findpost&ptid=1220112&pid=34645514
    var url: String? = null



    override fun toString(): String {
        return "HomeReply{" +
                "reply='" + reply + '\''.toString() +
                ", url='" + url + '\''.toString() +
                '}'.toString()
    }

    override fun isSameItem(other: Any): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        other as HomeReply

        if (url != other.url) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeReply

        if (reply != other.reply) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reply?.hashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        return result
    }

    companion object {

        fun fromHtmlElement(element: Element): HomeReply? {
            var reply: HomeReply? = null
            try {
                val tdReply = element.children().first() ?: return null
                val elesReply = tdReply.children()
                if (elesReply.size < 3) {
                    return null
                }
                reply = HomeReply()
                //reply
                val eleReply = elesReply[1]
                reply.reply = eleReply.text()
                //eg thread-1220112-1-1.html
                reply.url = eleReply.attr("href")
            } catch (e: Exception) {
                L.report(RuntimeException(element.toString(), e))
            }

            return reply
        }
    }
}
