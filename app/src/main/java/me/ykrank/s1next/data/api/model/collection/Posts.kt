package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.*
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.data.db.BlackListDbWrapper
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.util.StringUtil
import org.apache.commons.lang3.StringUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class Posts : Account {

    @JsonProperty("thread")
    var postListInfo: Thread? = null

    @JsonProperty("threadsortshow")
    var threadAttachment: ThreadAttachment? = null

    @JsonIgnore
    var postList: List<Post> = listOf()

    @JsonProperty("special_poll")
    val vote: Vote? = null

    constructor() {}

    @JsonCreator
    constructor(@JsonProperty("special_trade") trade: Map<Int, Any>?, @JsonProperty("postlist") postList: List<Post>?) {
        this.postList = filterPostList(postList)
        if (trade != null && postList != null && postList.isNotEmpty()) {
            val post = postList[0]
            if (trade.containsKey(post.id + 1)) {
                post.isTrade = true
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Posts

        if (postListInfo != other.postListInfo) return false
        if (threadAttachment != other.threadAttachment) return false
        if (postList != other.postList) return false
        if (vote != other.vote) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (postListInfo?.hashCode() ?: 0)
        result = 31 * result + (threadAttachment?.hashCode() ?: 0)
        result = 31 * result + (postList?.hashCode() ?: 0)
        result = 31 * result + (vote?.hashCode() ?: 0)
        return result
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class ThreadAttachment {

        @JsonProperty("threadsortname")
        var title: String? = null

        @JsonProperty("optionlist")
        var infoList: ArrayList<Info>? = null

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ThreadAttachment

            if (title != other.title) return false
            if (infoList != other.infoList) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title?.hashCode() ?: 0
            result = 31 * result + (infoList?.hashCode() ?: 0)
            return result
        }

        @PaperParcel
        @JsonIgnoreProperties(ignoreUnknown = true)
        class Info : PaperParcelable {

            @JsonIgnore
            @get:JsonGetter
            var label: String? = null

            @JsonIgnore
            @get:JsonGetter
            var value: String? = null

            constructor()

            @JsonCreator
            constructor(@JsonProperty("title") label: String?,
                        @JsonProperty("value") value: String?,
                        @JsonProperty("unit") unit: String?) {
                this.label = label
                this.value = StringUtil.unescapeNonBreakingSpace(value) + StringUtils.defaultString(unit)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Info

                if (label != other.label) return false
                if (value != other.value) return false

                return true
            }

            override fun hashCode(): Int {
                var result = label?.hashCode() ?: 0
                result = 31 * result + (value?.hashCode() ?: 0)
                return result
            }

            companion object {
                @JvmField
                val CREATOR = PaperParcelPosts_ThreadAttachment_Info.CREATOR
            }
        }
    }

    companion object {

        /**
         * @see .filterPost
         */
        fun filterPostList(oPosts: List<Post>?): List<Post> {
            val posts = ArrayList<Post>()
            if (oPosts != null) {
                for (post in oPosts) {
                    val fPost = filterPost(post)
                    if (fPost != null) {
                        posts.add(fPost)
                    }
                }
            }
            return posts
        }

        /**
         * 对数据源进行处理
         *
         *  * 标记黑名单用户
         *
         * 如果修改了过滤状态，则会返回不同的对象
         */
        fun filterPost(post: Post): Post? {
            var nPost: Post? = post
            val blackListWrapper = BlackListDbWrapper.getInstance()
            val blackList = blackListWrapper.getMergedBlackList(Integer.valueOf(post.authorId), post.authorName)
            if (blackList == null || blackList.post == BlackList.NORMAL) {
                if (post.isHide) {
                    nPost = post.clone()
                    nPost.isHide = false
                }
            } else if (blackList.post == BlackList.DEL_POST) {
                nPost = null
            } else if (blackList.post == BlackList.HIDE_POST) {
                if (!post.isHide) {
                    nPost = post.clone()
                    nPost.isHide = true
                }
                nPost!!.remark = blackList.remark
            }
            return nPost
        }
    }


}
