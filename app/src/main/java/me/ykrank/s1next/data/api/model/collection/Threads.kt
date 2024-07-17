package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.ThreadType
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.biz.ThreadBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList

@JsonIgnoreProperties(ignoreUnknown = true)
class Threads : Account {

    @JsonProperty("forum")
    var threadListInfo: Thread.ThreadListInfo? = null

    @JsonIgnore
    var threadList: List<Thread> = listOf()

    @JsonProperty("sublist")
    var subForumList: List<Forum> = listOf()

    @JsonIgnore
    var threadTypes: ArrayList<ThreadType>? = null

    constructor() {}

    @JsonCreator
    constructor(@JsonProperty("threadtypes") typesNode: JsonNode?,
                @JsonProperty("forum_threadlist") threadList: List<Thread>?) {
        val threadTypes = ArrayList<ThreadType>()
        try {
            val typeMap = androidx.collection.ArrayMap<String, String>()
            val fields = typesNode?.get("types")?.fields()
            if (fields != null) {
                while (fields.hasNext()) {
                    val entry = fields.next()
                    val type = ThreadType(entry.key, entry.value.asText())
                    threadTypes.add(type)
                    typeMap[type.typeId] = type.typeName
                }
            }
            if (threadList != null) {
                for (thread in threadList) {
                    thread.typeName = typeMap[thread.typeId]
                }
            }
        } catch (e: Exception) {
            L.report(e)
        }

        this.threadTypes = threadTypes
        this.threadList = getFilterThreadList(threadList)
    }

    companion object {

        /**
         * @see .getFilterThread
         */
        fun getFilterThreadList(oThreads: List<Thread>?): List<Thread> {
            val threads = ArrayList<Thread>()
            if (oThreads != null) {
                for (thread in oThreads) {
                    val fThread = getFilterThread(thread, false)
                    if (fThread != null) {
                        threads.add(fThread)
                    }
                }
            }
            return threads
        }

        /**
         * 对数据源进行处理
         *
         *  * 获取上次访问时回复数
         *
         * 如果修改了过滤状态，则会返回不同的对象
         */
        fun getFilterThread(oThread: Thread, copyed: Boolean = false): Thread? {
            LooperUtil.enforceOnWorkThread()
            var nThread: Thread = oThread
            val blackListWrapper = BlackListBiz.getInstance()
            when (blackListWrapper.getForumFlag(oThread.authorId, oThread.author, enableCache = true)) {
                BlackList.DEL_FORUM -> return null
                BlackList.HIDE_FORUM -> if (!oThread.isHide) {
                    if (copyed) {
                        nThread = oThread.clone() as Thread
                    }
                    nThread.isHide = true
                }
                BlackList.NORMAL -> if (oThread.isHide) {
                    if (copyed) {
                        nThread = oThread.clone() as Thread
                    }
                    nThread.isHide = false
                }
                else -> if (oThread.isHide) {
                    if (copyed) {
                        nThread = oThread.clone() as Thread
                    }
                    nThread.isHide = false
                }
            }

            val dbThread = ThreadBiz.instance.getWithThreadId(nThread.id?.toInt()
                    ?: 0)
            if (dbThread != null) {
                nThread.lastReplyCount = dbThread.lastCountWhenView
            }
            return nThread
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Threads

        if (threadListInfo != other.threadListInfo) return false
        if (threadList != other.threadList) return false
        if (subForumList != other.subForumList) return false
        if (threadTypes != other.threadTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (threadListInfo?.hashCode() ?: 0)
        result = 31 * result + (threadList?.hashCode() ?: 0)
        result = 31 * result + (subForumList?.hashCode() ?: 0)
        result = 31 * result + (threadTypes?.hashCode() ?: 0)
        return result
    }
}
