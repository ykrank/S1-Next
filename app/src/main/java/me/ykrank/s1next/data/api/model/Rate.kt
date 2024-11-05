package me.ykrank.s1next.data.api.model

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.util.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.ykrank.s1next.binding.TextViewBindingAdapter
import me.ykrank.s1next.data.api.ApiUtil
import me.ykrank.s1next.data.api.model.link.UserLink
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import org.jsoup.Jsoup
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.text.SimpleDateFormat
import java.util.Locale

@PaperParcel
@JsonIgnoreProperties(ignoreUnknown = true)
class Rate : PaperParcelable, StableIdModel {
    @JsonProperty("uid")
    var uid: String? = null

    @JsonProperty("uname")
    var uname: String? = null

    @JsonProperty("content")
    var content: String? = null

    @JsonProperty("score")
    var score: Int? = null

    @JsonProperty("time")
    var time: Long? = null

    @JsonProperty("_hide")
    @Post.HideFLag
    var hide: Int = Post.HIDE_NO

    @JsonProperty("_remark")
    var remark: String? = null

    val symbolScore: String
        get() {
            return if ((score ?: 0) < 0) "$score" else "+$score"
        }

    val blacklistScore: String
        get() {
            if (hide != Post.HIDE_NO) {
                return "*"
            }
            return symbolScore
        }

    fun blacklistContent(context: Context): String? {
        if (hide == Post.HIDE_NO) {
            return content
        }
        return TextViewBindingAdapter.buildBlacklistContent(context, hide, remark)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rate

        if (uid != other.uid) return false
        if (uname != other.uname) return false
        if (content != other.content) return false
        if (score != other.score) return false
        if (time != other.time) return false
        if (hide != other.hide) return false
        if (remark != other.remark) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid?.hashCode() ?: 0
        result = 31 * result + (uname?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (score ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + hide
        result = 31 * result + (remark?.hashCode() ?: 0)
        return result
    }

    override val stableId: Long
        get() = uid?.toLongOrNull() ?: RecyclerView.NO_ID


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
                        UserLink.parse(it.attr("href"))?.apply {
                            rate.uid = this.uid
                        }
                        rate.uname = it.text()
                    }
                    rate.time = df.parse(it.child(2).text())?.time
                    rate.content = it.child(3).text()
                    rates.add(rate)
                }
            } catch (e: Exception) {
                L.report(e)
            }

            return rates
        }

        suspend fun blacklist(blackListBiz: BlackListBiz, rates: List<Rate>): List<Rate> {
            return withContext(Dispatchers.IO) {
                rates.mapNotNull {
                    val blackList = blackListBiz.getMergedBlackList(
                        it.uid?.toIntOrNull() ?: -1, it.uname, enableCache = true
                    )
                    it.hide = Post.HIDE_NO
                    when (blackList?.post) {
                        BlackList.HIDE_POST -> it.apply {
                            hide = Post.HIDE_USER
                            remark = blackList.remark
                        }

                        BlackList.DEL_POST -> null
                        else -> it
                    }
                }
            }
        }
    }
}