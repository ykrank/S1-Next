package me.ykrank.s1next.data.api.model

import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.ApiUtil
import org.jsoup.Jsoup
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.text.SimpleDateFormat
import java.util.*

@PaperParcel
class Rate : PaperParcelable {
    var uid: String? = null
    var uname: String? = null
    var content: String? = null
    var score: Int? = null
    var time: Long? = null

    val symbolScore: String get() = if (score ?: 0 < 0) "$score" else "+$score"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rate

        if (uid != other.uid) return false
        if (uname != other.uname) return false
        if (content != other.content) return false
        if (score != other.score) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid?.hashCode() ?: 0
        result = 31 * result + (uname?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (score ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        return result
    }

    companion object {
        //2018-4-14 22:20
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

        @JvmField
        val CREATOR = PaperParcelRate.CREATOR

        fun fromHtml(rawHtml: String): List<Rate> {
            val rates = mutableListOf<Rate>()
            //remove html wrap
            val html = ApiUtil.replaceAjaxHeader(rawHtml)
            try {
                val document = Jsoup.parse(html)
                val rateElementList = document.select("table>tbody>tr")
                rateElementList.forEach {
                    val rate = Rate()
                    rate.score = it.child(0).text()?.let {
                        //In java 1.6, Integer.parseInt could not parse like "+1"
                        it.substring(3, it.length - 1).replace("+", "").trim()
                    }?.toInt()
                    it.child(1).child(0).also {
                        UserLink.parse(it.attr("href")).apply {
                            if (isPresent) {
                                rate.uid = get().uid
                            }
                        }
                        rate.uname = it.text()
                    }
                    rate.time = df.parse(it.child(2).text()).time
                    rate.content = it.child(3).text()
                    rates.add(rate)
                }
            } catch (e: Exception) {
                L.report(e)
            }

            return rates
        }
    }
}