package me.ykrank.s1next.data.api.model

import android.text.TextUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper
import org.jsoup.Jsoup
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

/**
 * Created by ykrank on 2016/7/31 0031.
 */
@PaperParcel
class ThreadType : PaperParcelable {
    var typeId: String? = null
    var typeName: String? = null

    constructor()

    constructor(typeId: String, typeName: String) {
        this.typeId = typeId
        this.typeName = typeName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ThreadType

        if (typeId != other.typeId) return false
        if (typeName != other.typeName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeId?.hashCode() ?: 0
        result = 31 * result + (typeName?.hashCode() ?: 0)
        return result
    }

    companion object {

        @JvmField
        val CREATOR = PaperParcelThreadType.CREATOR

        /**
         * Extracts [Quote] from XML string.
         *
         * @param html raw html
         * @return if no type, return empty list.
         */
        @Throws
        fun fromXmlString(html: String?): List<ThreadType> {
            LooperUtil.enforceOnWorkThread()
            val types = ArrayList<ThreadType>()
            try {
                val document = Jsoup.parse(html)
                HtmlDataWrapper.preAlertHtml(document)
                HtmlDataWrapper.preTreatHtml(document)
                val typeIdElements = document.select("#typeid>option")
                for (i in typeIdElements.indices) {
                    val element = typeIdElements[i]
                    val typeId = element.attr("value").trim()
                    val typeName = element.text()
                    types.add(ThreadType(typeId, typeName))
                }
            } catch (e: Exception) {
                L.leaveMsg("Source:" + html)
                throw e
            }

            return types
        }

        fun nameOf(types: List<ThreadType>?, typeId: String): String? {
            if (types == null || types.isEmpty()) {
                return null
            }
            for (type in types) {
                if (TextUtils.equals(type.typeId, typeId)) {
                    return type.typeName
                }
            }
            return null
        }
    }
}
