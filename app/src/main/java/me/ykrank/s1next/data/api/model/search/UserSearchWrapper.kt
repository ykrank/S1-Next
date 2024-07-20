package me.ykrank.s1next.data.api.model.search

import com.github.ykrank.androidtools.util.L.leaveMsg
import com.github.ykrank.androidtools.util.L.report
import me.ykrank.s1next.data.api.model.link.UserLink
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper.Companion.preTreatHtml
import org.jsoup.Jsoup

/**
 * Created by ykrank on 2017/04/01.
 */
data class UserSearchWrapper(
    var userSearchResults: List<UserSearchResult> = emptyList(),
    var errorMsg: String? = null
) {

    companion object {
        fun fromSource(source: String): UserSearchWrapper {
            val wrapper = UserSearchWrapper()
            val userSearchResults: MutableList<UserSearchResult> = ArrayList()
            try {
                val document = Jsoup.parse(source)
                preTreatHtml(document)
                val errorElements = document.select("div#messagetext")
                if (errorElements.size > 0) {
                    wrapper.errorMsg = errorElements.text()
                }
                //count
                val elements = document.select("li.bbda.cl")
                for (i in elements.indices) {
                    val element = elements[i]
                    try {
                        val result = UserSearchResult()
                        val userElement = element.child(1).child(0)
                        //uid
                        val href = userElement.attr("href")
                        val userLink = UserLink.parse(href)
                        result.uid = userLink!!.uid
                        //name
                        val name = userElement.text()
                        result.name = name
                        userSearchResults.add(result)
                    } catch (e: Exception) {
                        leaveMsg("Element:" + element.html())
                        report(e)
                    }
                }
            } catch (e: Exception) {
                leaveMsg("Source:$source")
                report(e)
            }
            wrapper.userSearchResults = userSearchResults
            return wrapper
        }
    }
}
