package me.ykrank.s1next.data.api.model

import com.google.common.base.Objects
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.model.wrapper.HomeReplyWebWrapper
import org.jsoup.nodes.Element

/**
 * Created by ykrank on 2017/2/4.
 * User's thread model
 */

class HomeThread : DiffSameItem, HomeReplyWebWrapper.HomeReplyItem, StableIdModel {
    var title: String? = null
    var forum: String? = null
    var view: String? = null
    var reply: String? = null
    var lastReplier: String? = null
    var lastReplyDate: String? = null
    //eg thread-1220112-1-1.html
    var url: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is HomeThread) return false
        val thread = o as HomeThread?
        return Objects.equal(title, thread!!.title) &&
                Objects.equal(forum, thread.forum) &&
                Objects.equal(view, thread.view) &&
                Objects.equal(reply, thread.reply) &&
                Objects.equal(lastReplier, thread.lastReplier) &&
                Objects.equal(lastReplyDate, thread.lastReplyDate) &&
                Objects.equal(url, thread.url)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(title, forum, view, reply, lastReplier, lastReplyDate, url)
    }

    override fun toString(): String {
        return "HomeThread{" +
                "title='" + title + '\''.toString() +
                ", forum='" + forum + '\''.toString() +
                ", view='" + view + '\''.toString() +
                ", reply='" + reply + '\''.toString() +
                ", lastReplier='" + lastReplier + '\''.toString() +
                ", lastReplyDate='" + lastReplyDate + '\''.toString() +
                ", url='" + url + '\''.toString() +
                '}'.toString()
    }

    override fun isSameItem(o: Any): Boolean {
        if (this === o) return true
        return if (o !is HomeThread) false else Objects.equal(url, o.url)
    }

    companion object {

        fun fromHtmlElement(element: Element): HomeThread? {
            var thread: HomeThread? = null
            try {
                if (element.children().size < 5) {
                    return null
                }
                thread = HomeThread()
                //title
                val title = element.child(1).child(0)
                thread.title = title.text()
                //eg thread-1220112-1-1.html
                thread.url = title.attr("href")
                //forum
                val forum = element.child(2).child(0)
                thread.forum = forum.text()
                //num
                val num = element.child(3)
                thread.reply = num.child(0).text()
                thread.view = num.child(1).text()
                //by
                val by = element.child(4)
                thread.lastReplier = by.child(0).child(0).text()
                thread.lastReplyDate = by.child(1).child(0).text()
            } catch (e: Exception) {
                L.report(e)
            }

            return thread
        }
    }
}
