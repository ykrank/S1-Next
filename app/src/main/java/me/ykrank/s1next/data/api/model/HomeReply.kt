package me.ykrank.s1next.data.api.model

import com.google.common.base.Objects
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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val homeReply = o as HomeReply?
        return Objects.equal(reply, homeReply!!.reply) && Objects.equal(url, homeReply.url)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(reply, url)
    }

    override fun toString(): String {
        return "HomeReply{" +
                "reply='" + reply + '\''.toString() +
                ", url='" + url + '\''.toString() +
                '}'.toString()
    }

    override fun isSameItem(o: Any): Boolean {
        if (this === o) return true
        return if (o !is HomeReply) false else Objects.equal(url, o.url)
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
