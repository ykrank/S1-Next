package me.ykrank.s1next.data.api.model.wrapper

import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.App
import me.ykrank.s1next.data.api.ApiException
import me.ykrank.s1next.view.event.NoticeRefreshEvent
import org.jsoup.nodes.Document
import java.util.regex.Pattern

/**
 * Parse html extra data
 * Created by ykrank on 2017/6/7.
 */
class HtmlDataWrapper {
    var notice: Int? = null

    companion object {
        fun preTreatHtml(document: Document): HtmlDataWrapper {
            val result = HtmlDataWrapper()
            try {
                val elements = document.select("#myprompt")
                if (!elements.isEmpty()) {
                    val noticeStr = elements[0].text()
                    val pattern = Pattern.compile("\\((\\d*)\\)")
                    val matcher = pattern.matcher(noticeStr)
                    if (matcher.find()) {
                        result.notice = matcher.group(1)?.toInt() ?: 0
                        notifyData(result)
                    }
                }
            } catch (e: Exception) {
                L.report(e)
            }
            return result
        }

        fun notifyData(data: HtmlDataWrapper) {
            if (data.notice != null) {
                App.preAppComponent.rxBus.post(NoticeRefreshEvent::class.java, NoticeRefreshEvent(null, (data.notice?:0) > 0))
            }
        }

        /**
         * Check if server alert
         */
        @Throws(ApiException.ApiServerException::class)
        fun preAlertHtml(document: Document) {
            val errorElements = document.select("div#messagetext")
            if (errorElements.size > 0) {
                throw ApiException.ApiServerException(errorElements.text())
            }
        }

        /**
         * Check if server alert in ajax
         */
        @Throws(ApiException.ApiServerException::class)
        fun preAlertAjaxHtml(document: Document) {
            val errorElements = document.select("div.alert_error")
            if (errorElements.size > 0) {
                throw ApiException.ApiServerException(errorElements[0].text())
            }
        }
    }
}
