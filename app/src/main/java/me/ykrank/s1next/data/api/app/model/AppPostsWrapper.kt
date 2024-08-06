package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import me.ykrank.s1next.data.api.business.PostFilter
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by ykrank on 2017/7/22.
 */
class AppPostsWrapper() : AppListWrapper<AppPost>() {
    @JsonIgnore
    var thread: AppThread? = null

    @JsonCreator
    constructor(@JsonProperty("data") data: BaseAppList<AppPost>?) : this() {
        if (data != null) {
            data.list = filterPostList(data.list)
        }
        this.data = data
    }

    companion object {
        /**
         * @see .filterPost
         */
        fun filterPostList(oPosts: ArrayList<AppPost>): ArrayList<AppPost> {
            return oPosts.mapNotNullTo(arrayListOf(), { filterPost(it) })
        }

        /**
         * 对数据源进行处理
         *
         *  * 标记黑名单用户
         *
         * 如果修改了过滤状态，则会返回不同的对象
         */
        fun filterPost(post: AppPost): AppPost? {
            var nPost: AppPost? = post
            val blackListWrapper = BlackListBiz.getInstance()
            val blackList = blackListWrapper.getMergedBlackList(post.authorId, post.author, enableCache = true)
            if (blackList == null || blackList.post == BlackList.NORMAL) {
                if (post.hide) {
                    nPost = post.clone()
                    nPost.hide = false
                }
            } else if (blackList.post == BlackList.DEL_POST) {
                nPost = null
            } else if (blackList.post == BlackList.HIDE_POST) {
                if (!post.hide) {
                    nPost = post.clone()
                    nPost.hide = true
                }
                nPost?.remark = blackList.remark
            }
            return nPost
        }
    }
}

@PaperParcel
class AppPost() : PaperParcelable, Cloneable, DiffSameItem, StableIdModel {
    @JsonProperty("pid")
    var pid: Int = 0
    @JsonProperty("fid")
    var fid: Int = 0
    @JsonProperty("tid")
    var tid: Int = 0
    @JsonProperty("author")
    var author: String? = null
    @JsonProperty("authorid")
    var authorId: Int = 0
    @JsonProperty("dateline")
    var dateline: Long = 0
    @JsonIgnore
    var message: String? = null
        @JsonGetter("message")
        get

    @JsonProperty("status")
    var status: Int = 0
    @JsonProperty("position")
    var position: Int = 0
    @JsonProperty("blocked")
    var blocked: Boolean = false
    @JsonProperty("e")
    var e: Int = 0
    @JsonProperty("customstatus")
    var customStatus: String? = null
    @JsonProperty("grouptitle")
    var groupTitle: String? = null
    @JsonProperty("gorupid")
    var groupId: Int = 0
    @JsonProperty("avatarurl")
    var avatarUrl: String? = null
    @JsonIgnore
    var hide: Boolean = false
    @JsonIgnore
    var remark: String? = null
    @JsonIgnore
    var trade: Boolean = false
    @JsonIgnore
    var extraHtml: String? = null

    override val stableId: Long
        get() = pid.toLong()

    @JsonCreator
    constructor(@JsonProperty("message") message: String?) : this() {
        if (message.isNullOrBlank()) {
            this.message = message
        } else {
            var msg = PostFilter.hideBlackListQuote(message)
            msg = PostFilter.replaceBilibiliTag(msg)
            this.message = msg
        }
    }

    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AppPost
        if (pid != other.pid) return false
        if (author != other.author) return false
        if (authorId != other.authorId) return false

        return true
    }

    override public fun clone(): AppPost {
        return super.clone() as AppPost
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AppPost

        if (pid != other.pid) return false
        if (fid != other.fid) return false
        if (tid != other.tid) return false
        if (author != other.author) return false
        if (authorId != other.authorId) return false
        if (dateline != other.dateline) return false
        if (status != other.status) return false
        if (position != other.position) return false
        if (blocked != other.blocked) return false
        if (e != other.e) return false
        if (customStatus != other.customStatus) return false
        if (groupTitle != other.groupTitle) return false
        if (groupId != other.groupId) return false
        if (avatarUrl != other.avatarUrl) return false
        if (hide != other.hide) return false
        if (remark != other.remark) return false
        if (trade != other.trade) return false
        if (extraHtml != other.extraHtml) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pid
        result = 31 * result + fid
        result = 31 * result + tid
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + authorId
        result = 31 * result + dateline.hashCode()
        result = 31 * result + status
        result = 31 * result + position
        result = 31 * result + blocked.hashCode()
        result = 31 * result + e
        result = 31 * result + (customStatus?.hashCode() ?: 0)
        result = 31 * result + (groupTitle?.hashCode() ?: 0)
        result = 31 * result + groupId
        result = 31 * result + (avatarUrl?.hashCode() ?: 0)
        result = 31 * result + hide.hashCode()
        result = 31 * result + (remark?.hashCode() ?: 0)
        result = 31 * result + trade.hashCode()
        result = 31 * result + (extraHtml?.hashCode() ?: 0)
        return result
    }

    companion object {
        @JvmField
        val CREATOR = PaperParcelAppPost.CREATOR
    }

}
