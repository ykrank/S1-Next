package me.ykrank.s1next.data.api.model

import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.ApiUtil
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper
import org.jsoup.Jsoup

/**
 * Created by ykrank on 2019/5/23.
 */
class WebBlackListInfo {
    var users: List<Pair<String, String>> = listOf()
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
                        val userLink = UserLink.parse(ue.attr("href")).orNull()
                        return@let userLink?.let { ul ->
                            Pair(ul.uid, ue.text())
                        }
                    }
                }



            } catch (e: Exception) {
                L.report(e)
                throw e
            }

            return info
        }
    }
}
