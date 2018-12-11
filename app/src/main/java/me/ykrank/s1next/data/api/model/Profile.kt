package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.github.ykrank.androidtools.util.L
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ykrank on 2017/1/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Profile : Account {

    var homeUsername: String? = null
    var homeUid: String? = null
    var signHtml: String? = null
    var friends: Int = 0
    var replies: Int = 0
    var threads: Int = 0
    var groupTitle: String? = null
    var onlineHour: Int = 0
    var regDate: Long? = null
    var lastVisitDate: Long? = null
    var lastActiveDate: Long? = null
    var lastPostDate: Long? = null
    @JsonIgnore
    var stats: List<Pair<String, String>> = listOf()
    @JsonIgnore
    var manager: List<String>? = null
    val managerString: String?
        get() = manager.toString()

    constructor()

    @JsonCreator
    constructor(@JsonProperty("extcredits") extCredits: JsonNode, @JsonProperty("space") space: JsonNode) {
        this.homeUsername = space.get("username")?.asText()
        this.homeUid = space.get("uid")?.asText()
        this.signHtml = space.get("sightml")?.asText()
        val posts = space.get("posts")?.asInt() ?: -1
        this.friends = space.get("friends")?.asInt() ?: -1
        this.threads = space.get("threads")?.asInt() ?: -1
        this.replies = posts - threads
        this.groupTitle = space.get("group")?.get("grouptitle")?.asText()
        this.onlineHour = space.get("oltime")?.asInt() ?: -1
        this.regDate = space.get("regdate")?.asLong()
        this.lastVisitDate = space.get("lastvisit")?.asLong()
        this.lastActiveDate = space.get("lastactivity")?.asLong()
        this.lastPostDate = space.get("lastpost")?.asLong()
    }

    companion object {
        //2018-4-14 22:20
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

        fun fromHtml(html: String): Profile {
            val profile = Profile()
            try {
                val document = Jsoup.parse(html)
                val profileDiv = document.select("div.u_profile")[0]
                //<div class="pbm mbm bbda cl">
                val baseElement = profileDiv.child(0)
                //<h2 class="mbn">
                val nameEle = baseElement.child(0)
                profile.homeUsername = nameEle.textNodes()[0]?.text()?.trim()
                profile.homeUid = nameEle.selectFirst("span").text().trim().let {
                    it.substring(6, it.length - 1)
                }
                profile.signHtml = baseElement.child(2)?.selectFirst("table")?.html()
                val countEle = baseElement.child(3).child(0)
                profile.friends = countEle.child(1).text().trim().substring(4).toInt()
                profile.replies = countEle.child(3).text().trim().substring(4).toInt()
                profile.threads = countEle.child(5).text().trim().substring(4).toInt()

                profileDiv.children().forEach { ele ->
                    val h2 = ele.selectFirst("h2.mbn")
                    when (h2?.text()?.trim()) {
                        "管理以下版块" -> {
                            profile.manager = ele.select("a").mapNotNull { it.text() }
                        }
                        "活跃概况" -> {
                            profile.groupTitle = ele.child(1).selectFirst("span").text()
                            //<ul id="pbbs" class="pf_l">
                            val pbbsEle = profileDiv.getElementById("pbbs")
                            pbbsEle.children().forEach {
                                when (it.child(0).text().trim()) {
                                    "在线时间" -> profile.onlineHour = it.textNodes()[0].text().trim().let {
                                        it.substring(0, it.length - 3)
                                    }.toInt()
                                    "注册时间" -> profile.regDate = it.textNodes()[0].text().trim().let {
                                        df.parse(it).time / 1000
                                    }
                                    "最后访问" -> profile.lastVisitDate = it.textNodes()[0].text().trim().let {
                                        df.parse(it).time / 1000
                                    }
                                    "上次活动时间" -> profile.lastActiveDate = it.textNodes()[0].text().trim().let {
                                        df.parse(it).time / 1000
                                    }
                                    "上次发表时间" -> profile.lastPostDate = it.textNodes()[0].text().trim().let {
                                        df.parse(it).time / 1000
                                    }
                                }
                            }
                        }
                    }
                }
                //统计信息
                val postElement = profileDiv.getElementById("psts")
                val postUlElement = postElement.selectFirst("ul.pf_l")
                val stats = mutableListOf<Pair<String, String>>()
                postUlElement.children().forEach {
                    val title = it.child(0).text().trim()
                    val content = it.textNodes()[0].text().trim()
                    if (!title.isEmpty()) {
                        stats.add(Pair(title, content))
                    }
                }
                profile.stats = stats
            } catch (e: Exception) {
                L.report(e)
            }

            return profile
        }
    }
}
