package me.ykrank.s1next.data.api.model.collection

import androidx.annotation.WorkerThread
import com.fasterxml.jackson.annotation.*
import com.github.ykrank.androidtools.util.StringUtil
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.data.db.BlackListDbWrapper
import me.ykrank.s1next.data.db.BlackWordDbWrapper
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import org.apache.commons.lang3.StringUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class Posts : Account {

    @JsonIgnore
    var postListInfo: Thread? = null
        @JsonSetter("thread") set(p) {
            this.postList.forEach {
                if (p?.author == it.authorName) {
                    it.isOpPost = true
                }
            }
            field = p
        }

    @JsonProperty("threadsortshow")
    var threadAttachment: ThreadAttachment? = null

    @JsonIgnore
    var postList: List<Post> = listOf()

    @JsonProperty("special_poll")
    val vote: Vote? = null

    constructor() {}

    @JsonCreator
    constructor(@JsonProperty("special_trade") trade: Map<Int, Any>?,
                @JsonProperty("postlist") postList: List<Post>?) {
        this.postList = filterPostList(postList)
        if (trade != null && postList != null && postList.isNotEmpty()) {
            val post = postList[0]
            if (trade.containsKey(post.id + 1)) {
                post.isTrade = true
            }
        }
    }

    fun initCommentCount(commentCountMap: Map<Int, Int?>) {
        this.postList.forEach {
            if (commentCountMap.containsKey(it.id)) {
                it.rates = listOf()
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
        result = 31 * result + postList.hashCode()
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
        @WorkerThread
        fun filterPostList(oPosts: List<Post>?): List<Post> {
            val posts = ArrayList<Post>()
            if (oPosts != null) {
                val blackWords = BlackWordDbWrapper.instance.getAllNotNormalBlackWord()
                for (post in oPosts) {
                    val fPost = filterPost(post, false, blackWords)
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
         *  * 回复引用新版替换回老版样式
         *  * 过滤屏蔽词（已屏蔽的对象不会在修改屏蔽词后自动更新，而必须是重新的原始对象）
         *
         * 如果修改了过滤状态，则会返回不同的对象
         */
        @WorkerThread
        fun filterPost(post: Post, clone: Boolean = false, blackWords: List<BlackWord>? = null): Post? {
            var nPost: Post = post
            val blackListWrapper = BlackListDbWrapper.getInstance()
            val blackList = blackListWrapper.getMergedBlackList(post.authorId?.toIntOrNull()
                    ?: -1, post.authorName)
            if (blackList == null || blackList.post == BlackList.NORMAL) {
                if (post.hide == Post.Hide_User) {
                    if (clone) {
                        nPost = post.clone()
                    }
                    nPost.hide = Post.Hide_Normal
                }
            } else if (blackList.post == BlackList.DEL_POST) {
                return null
            } else if (blackList.post == BlackList.HIDE_POST) {
                if (post.hide != Post.Hide_User) {
                    if (clone) {
                        nPost = post.clone()
                    }
                    nPost.hide = Post.Hide_User
                }
                nPost.remark = blackList.remark
            }

            val reply = nPost.reply
            if (reply != null && nPost.hide == Post.Hide_Normal) {
                val mBlackWords = blackWords
                        ?: BlackWordDbWrapper.instance.getAllNotNormalBlackWord()
                mBlackWords.forEach {
                    val word = it.word
                    if (!word.isNullOrEmpty() && it.stat != BlackWord.NORMAL) {
                        if (reply.contains(word, false)) {
                            if (it.stat == BlackWord.DEL) {
                                return null
                            } else if (it.stat == BlackWord.HIDE) {
                                //Only clone if not cloned before
                                if (clone && nPost === post) {
                                    nPost = post.clone()
                                }
                                nPost.hide = Post.Hide_Word
                                return@forEach
                            }
                        }
                    }
                }
            }

            return nPost
        }
    }


}
