package me.ykrank.s1next.data.api.model

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

    override fun isSameItem(other: Any): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        other as HomeThread

        if (url != other.url) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeThread

        if (title != other.title) return false
        if (forum != other.forum) return false
        if (view != other.view) return false
        if (reply != other.reply) return false
        if (lastReplier != other.lastReplier) return false
        if (lastReplyDate != other.lastReplyDate) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (forum?.hashCode() ?: 0)
        result = 31 * result + (view?.hashCode() ?: 0)
        result = 31 * result + (reply?.hashCode() ?: 0)
        result = 31 * result + (lastReplier?.hashCode() ?: 0)
        result = 31 * result + (lastReplyDate?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        return result
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
