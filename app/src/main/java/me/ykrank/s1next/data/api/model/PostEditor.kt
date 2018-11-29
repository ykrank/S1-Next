package me.ykrank.s1next.data.api.model

import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper
import org.jsoup.Jsoup
import java.util.*

/**
 * Model for edit post
 * Created by ykrank on 2017/4/12.
 */

class PostEditor {
    /**
     * if no element "#typeid>option", this is null
     */
    var threadTypes: List<ThreadType>? = null
    var typeIndex: Int = 0
    var readPermTypes: List<String>? = null
    var readPermIndex: Int = 0
    var subject: String? = null
    var message: String? = null

    companion object {

        @Throws
        fun fromHtml(html: String): PostEditor {
            LooperUtil.enforceOnWorkThread()
            val editor = PostEditor()
            try {
                val document = Jsoup.parse(html)
                HtmlDataWrapper.preTreatHtml(document)
                HtmlDataWrapper.preAlertHtml(document)
                //thread types
                val typeIdElements = document.select("#typeid>option")
                val threadTypes = ArrayList<ThreadType>()
                for (i in typeIdElements.indices) {
                    val element = typeIdElements[i]
                    val typeId = element.attr("value").trim()
                    val typeName = element.text()
                    if ("selected" == element.attr("selected").trim()) {
                        editor.typeIndex = i
                    }
                    threadTypes.add(ThreadType(typeId, typeName))
                }
                editor.threadTypes = threadTypes
                //subject
                val subjectElements = document.select("input#subject")
                if (subjectElements.size > 0) {
                    editor.subject = subjectElements[0].attr("value")
                }
                //message
                val messageElements = document.select("textarea#e_textarea")
                if (messageElements.size > 0) {
                    editor.message = messageElements[0].text()
                }
                //read permission
                val permElements = document.select("#readperm>option")
                val permTypes = hashSetOf<String>()
                var readPerm = ""
                for (i in permElements.indices) {
                    val element = permElements[i]
                    val perm = element.attr("value").trim()
                    if ("selected" == element.attr("selected").trim()) {
                        readPerm = perm
                    }
                    permTypes.add(perm)
                }
                val readPermTypes = permTypes.toList().sortedBy { it.toIntOrNull() }
                editor.readPermIndex = readPermTypes.indexOf(readPerm)
                editor.readPermTypes = readPermTypes
            } catch (e: Exception) {
                L.leaveMsg("Source:$html")
                throw e
            }

            return editor
        }
    }
}
