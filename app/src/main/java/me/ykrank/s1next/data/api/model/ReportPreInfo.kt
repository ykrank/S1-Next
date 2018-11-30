package me.ykrank.s1next.data.api.model

import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.ApiUtil
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper
import org.jsoup.Jsoup


class ReportPreInfo {

    val reason: List<String> = listOf("广告垃圾", "违规内容", "恶意灌水", "重复发帖", "其他")
    var fields: Map<String, String> = hashMapOf()

    companion object {

        @Throws
        fun fromHtml(tid: String?, pageNum: Int, html: String): ReportPreInfo {
            var html = html
            val info = ReportPreInfo()
            //remove html wrap
            html = ApiUtil.replaceAjaxHeader(html)
            try {
                val document = Jsoup.parse(html)
                HtmlDataWrapper.preAlertAjaxHtml(document)

                val fields: HashMap<String, String> = hashMapOf()
                val input = document.select("input")
                input.forEach {
                    fields[it.attr("name")] = it.attr("value")
                }

                if (!tid.isNullOrEmpty()) {
                    val refer = fields["referer"]
                    if (refer != null) {
                        fields["referer"] = refer.replaceFirst("/./", "/thread-$tid-$pageNum-1.html")
                    }
                }

                info.fields = fields

                //TODO referer和report_message需要填充
            } catch (e: Exception) {
                L.leaveMsg(html)
                throw e
            }

            return info
        }
    }
}
