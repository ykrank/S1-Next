package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.core.JsonParseException
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.ApiUtil
import org.jsoup.Jsoup
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

/**
 * Created by ykrank on 2017/3/19.
 */
@PaperParcel
class RatePreInfo : PaperParcelable {
    var formHash: String? = null
    var tid: String? = null
    var pid: String? = null
    var refer: String? = null
    var handleKey: String? = null
    var minScore: Int = 0
    var maxScore: Int = 0
    var totalScore: String? = null
    var reasons: List<String> = arrayListOf()
    var isChecked: Boolean = false
    var isDisabled: Boolean = false
    var scoreChoices: List<String> = arrayListOf()
    var alertError: String? = null

    fun setScoreChoices() {
        val list = ArrayList<String>()
        for (i in minScore..maxScore) {
            if (i != 0) {
                list.add(i.toString())
            }
        }
        this.scoreChoices = list
    }

    override fun toString(): String {
        return "RatePreInfo{" +
                "formHash='" + formHash + '\''.toString() +
                ", tid='" + tid + '\''.toString() +
                ", pid='" + pid + '\''.toString() +
                ", refer='" + refer + '\''.toString() +
                ", handleKey='" + handleKey + '\''.toString() +
                ", minScore=" + minScore +
                ", maxScore=" + maxScore +
                ", totalScore=" + totalScore +
                ", reasons=" + reasons +
                ", checked=" + isChecked +
                ", disabled=" + isDisabled +
                ", scoreChoices=" + scoreChoices +
                ", alertError='" + alertError + '\''.toString() +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RatePreInfo

        if (formHash != other.formHash) return false
        if (tid != other.tid) return false
        if (pid != other.pid) return false
        if (refer != other.refer) return false
        if (handleKey != other.handleKey) return false
        if (minScore != other.minScore) return false
        if (maxScore != other.maxScore) return false
        if (totalScore != other.totalScore) return false
        if (reasons != other.reasons) return false
        if (isChecked != other.isChecked) return false
        if (isDisabled != other.isDisabled) return false
        if (scoreChoices != other.scoreChoices) return false
        if (alertError != other.alertError) return false

        return true
    }

    override fun hashCode(): Int {
        var result = formHash?.hashCode() ?: 0
        result = 31 * result + (tid?.hashCode() ?: 0)
        result = 31 * result + (pid?.hashCode() ?: 0)
        result = 31 * result + (refer?.hashCode() ?: 0)
        result = 31 * result + (handleKey?.hashCode() ?: 0)
        result = 31 * result + (minScore?.hashCode() ?: 0)
        result = 31 * result + (maxScore?.hashCode() ?: 0)
        result = 31 * result + (totalScore?.hashCode() ?: 0)
        result = 31 * result + (reasons?.hashCode() ?: 0)
        result = 31 * result + isChecked.hashCode()
        result = 31 * result + isDisabled.hashCode()
        result = 31 * result + (scoreChoices?.hashCode() ?: 0)
        result = 31 * result + (alertError?.hashCode() ?: 0)
        return result
    }

    companion object {

        @JvmField
        val CREATOR = PaperParcelRatePreInfo.CREATOR

        fun fromHtml(html: String): RatePreInfo {
            var html = html
            val info = RatePreInfo()
            //remove html wrap
            html = ApiUtil.replaceAjaxHeader(html)
            try {
                val document = Jsoup.parse(html)
                //alert error
                var elements = document.select("div.alert_error")
                if (!elements.isEmpty()) {
                    info.alertError = elements[0].text()
                    return info
                }

                elements = document.select("#rateform>input")
                if (elements.size != 5) {
                    throw JsonParseException(null, "#rateform>input size is " + elements.size)
                }
                info.formHash = elements[0].attr("value")
                info.tid = elements[1].attr("value")
                info.pid = elements[2].attr("value")
                info.refer = elements[3].attr("value")
                info.handleKey = elements[4].attr("value")
                //score
                elements = document.select(".dt.mbm>tbody>tr")
                if (elements.size != 2) {
                    throw JsonParseException(null, ".dt.mbm>tbody>tr size is " + elements.size)
                }
                val scoreElements = elements[1].children()
                val minMaxScoreString = scoreElements[2].text().trim { it <= ' ' }
                val splitResult = minMaxScoreString.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                info.minScore = splitResult[0].trim { it <= ' ' }.toIntOrNull() ?: 0
                info.maxScore = splitResult[1].trim { it <= ' ' }.toIntOrNull() ?: 0
                info.totalScore = scoreElements[3].text().trim { it <= ' ' }
                //reasons
                val reasons = ArrayList<String>()
                elements = document.select("#reasonselect>li")
                for (element in elements) {
                    reasons.add(element.text())
                }
                info.reasons = reasons
                //checkbox
                elements = document.select("#sendreasonpm")
                if (elements.size != 1) {
                    throw JsonParseException(null, "#sendreasonpm size is " + elements.size)
                }
                val checkBoxElement = elements[0]
                info.isChecked = "checked".equals(checkBoxElement.attr("checked"), ignoreCase = true)
                info.isDisabled = "disabled".equals(checkBoxElement.attr("disabled"), ignoreCase = true)

                info.setScoreChoices()
            } catch (e: Exception) {
                L.report(e)
            }

            return info
        }
    }
}
