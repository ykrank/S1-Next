package me.ykrank.s1next.data.api.model

import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.ApiUtil
import me.ykrank.s1next.data.api.model.link.UserLink
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper
import org.jsoup.Jsoup

/**
 * Created by ykrank on 2019/5/23.
 */
class WebBlackListInfo {
    var users: List<Pair<Int, String>> = listOf()
    var page: Int = 1
    var max: Int = 1


    companion object {

        fun fromHtml(html: String): WebBlackListInfo {
            var html = html
            val info = WebBlackListInfo()
            //remove html wrap
            html = ApiUtil.replaceAjaxHeader(html)
            try {
                val document = Jsoup.parse(html)
                HtmlDataWrapper.preAlertAjaxHtml(document)

                val elements = document.select("#friend_ul li")
                info.users = elements.mapNotNull {
                    val userElement = it.selectFirst("h4>a")
                    return@mapNotNull userElement?.let { ue ->
                        val userLink = UserLink.parse(ue.attr("href"))
                        return@let userLink?.let { ul ->
                            Pair(ul.uid.toInt(), ue.text())
                        }
                    }
                }

                val pageElement = document.selectFirst(".pg>label")
                if (pageElement != null) {
                    info.page = pageElement.selectFirst("input")?.attr("value")?.toInt() ?: 1
                    info.max = pageElement.selectFirst("span")?.text()?.replace(" ", "")?.let {
                        it.substring(
                            1, it
                                .length - 1
                        )
                    }?.trim()?.toInt() ?: 1
                }

            } catch (e: Exception) {
                L.report(e)
                throw e
            }

            return info
        }
    }
}
