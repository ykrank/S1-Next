package me.ykrank.s1next.data.api.empty

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.CacheStrategy
import com.github.ykrank.androidtools.data.Resource
import com.github.ykrank.androidtools.data.Source
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.ApiUtil
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper
import me.ykrank.s1next.data.api.model.wrapper.RatePostsWrapper
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper
import me.ykrank.s1next.data.db.biz.CacheBiz
import me.ykrank.s1next.data.db.dbmodel.Cache
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.util.toJson

class S1ApiCacheProvider(
    private val downloadPerf: DownloadPreferencesManager,
    private val s1Service: S1Service,
    private val cacheBiz: CacheBiz,
    private val user: User,
    private val jsonMapper: ObjectMapper,
) : ApiCacheProvider {

    override suspend fun getForumGroupsWrapper(param: CacheParam?): Flow<Resource<ForumGroupsWrapper>> {
        return getFlow(CacheType.ForumGroups, param, ForumGroupsWrapper::class.java, api = {
            s1Service.getForumGroupsWrapper()
        }, setValidator = {
            !it.data?.forumList.isNullOrEmpty()
        })
    }

    override suspend fun getThreadsWrapper(
        forumId: String?,
        typeId: String?,
        page: Int,
        param: CacheParam?
    ): Flow<Resource<ThreadsWrapper>> {
        return getFlow(CacheType.Threads, param, ThreadsWrapper::class.java, api = {
            s1Service.getThreadsWrapper(forumId, typeId, page)
        }, setValidator = {
            !it.data?.threadList.isNullOrEmpty()
        })
    }

    override suspend fun getPostsWrapper(
        threadId: String?,
        authorId: String?,
        page: Int,
        param: CacheParam?
    ): Flow<Resource<PostsWrapper>> {
        val ratePostTask = coroutineScope {
            async(Dispatchers.IO) {
                runCatching {
                    s1Service.getPostsWrapperNew(threadId, page, authorId).let {
                        jsonMapper.readValue(it, RatePostsWrapper::class.java)
                    }
                }
            }
        }
        return getFlow(CacheType.Posts, param, PostsWrapper::class.java, api = {
            s1Service.getPostsWrapper(threadId, page, authorId)
        }, setValidator = {
            // 需要后处理才能更新缓存
            false
        }).map {
            if (it.source.isCloud()) {
                withContext(Dispatchers.IO) {
                    var hasError = false
                    val postWrapper = it.data
                    val ratePostWrapper = ratePostTask.await().apply {
                        if (this.isFailure) {
                            hasError = true
                        }
                    }

                    //Set comment init info(if it has comment)
                    ratePostWrapper.getOrNull()?.data?.commentCountMap?.apply {
                        postWrapper?.data?.initCommentCount(this)
                    }

                    val postList = postWrapper?.data?.postList
                    if (!postList.isNullOrEmpty()) {
                        val post = postList[0]
                        if (post.isTrade) {
                            post.extraHtml = ""
                            runCatching {
                                s1Service.getTradePostInfo(threadId, post.id + 1).apply {
                                    post.extraHtml = ApiUtil.replaceAjaxHeader(this)
                                }
                            }.apply {
                                if (this.isFailure) {
                                    hasError = true
                                }
                            }
                        }
                    }
                    if (!hasError && postWrapper != null) {
                        cacheBiz.saveTextZip(
                            getKey(CacheType.Posts, param),
                            jsonMapper.writeValueAsString(postWrapper),
                            maxSize = downloadPerf.totalDataCacheSize
                        )
                    }
                }
            }
            it
        }
    }

    override fun getPostsWrapper(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<Resource<String>> {
        return oWrapper.map {
            Resource.Success(Source.CLOUD, it)
        }
    }

    override fun getPostsWrapperNew(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<Resource<String>> {
        return oWrapper.map {
            Resource.Success(Source.CLOUD, it)
        }
    }

    private fun getKey(type: CacheType, param: CacheParam?): String {
        return "u${user.uid ?: 0}#${type.type}#${param?.keys?.joinToString(",") ?: ""}"
    }

    /**
     * 不指定CacheParam时，优先从网络获取
     */
    private fun <T : Any> getFlow(
        type: CacheType,
        param: CacheParam?,
        cls: Class<T>,
        api: suspend () -> String,
        getValidator: ((data: T) -> Boolean)? = null,
        setValidator: ((data: T) -> Boolean)? = null,
    ): Flow<Resource<T>> {
        val key = getKey(type, param)
        val cacheStrategy = param?.strategy ?: CacheStrategy.NET_FIRST
        return flow {
            val cacheData: Cache? by lazy {
                cacheBiz.getTextZipByKey(key)
            }

            fun parseCache(): T? {
                val json = cacheData?.text
                if (!json.isNullOrEmpty()) {
                    val data = jsonMapper.readValue(json, cls)
                    if (data != null &&
                        (getValidator == null || getValidator(data))
                    ) {
                        return data
                    }
                }
                return null
            }

            // 优先拉取缓存
            val cacheFirst = downloadPerf.netCacheEnable && !cacheStrategy.strategy.ignoreCache
            if (cacheFirst) {
                runCatching {
                    val expired =
                        System.currentTimeMillis() - (cacheData?.timestamp
                            ?: 0) > cacheStrategy.strategy.expired.inWholeMilliseconds
                    if (!expired) {
                        parseCache()?.apply {
                            emit(Resource.Success<T>(Source.PERSISTENCE, this))
                        }
                    }
                }
            }

            // 拉取网络数据
            val cacheFallbackEnable: Boolean by lazy {
                val cacheFallback =
                    downloadPerf.netCacheEnable && !cacheStrategy.fallbackStrategy.ignoreCache
                if (cacheFallback) {
                    val expired =
                        System.currentTimeMillis() - (cacheData?.timestamp
                            ?: 0) > cacheStrategy.fallbackStrategy.expired.inWholeMilliseconds
                    !expired
                } else {
                    false
                }
            }

            var fallbackSuccess = false
            val data = runCatching {
                api().let {
                    val data = it.toJson(cls)
                    if (getValidator != null && !getValidator(data)) {
                        // 无效的数据降级到缓存
                        if (cacheFallbackEnable && !cacheFirst) {
                            parseCache()?.apply {
                                fallbackSuccess = true
                                emit(Resource.Success<T>(Source.PERSISTENCE, this))
                            }
                        }
                    }
                    if (setValidator == null || setValidator(data)) {
                        // 有效的数据更新到缓存
//                        cacheBiz.saveTextZip(key, it, maxSize = downloadPerf.totalDataCacheSize)
                        cacheBiz.saveTextZip(
                            key,
                            jsonMapper.writeValueAsString(data),
                            maxSize = downloadPerf.totalDataCacheSize
                        )
                    }
                    data
                }
            }.onFailure {
                // 失败的请求降级到缓存
                if (cacheFallbackEnable && !cacheFirst) {
                    parseCache()?.apply {
                        fallbackSuccess = true
                        emit(Resource.Success<T>(Source.PERSISTENCE, this))
                    }
                }
            }
            if (!fallbackSuccess) {
                emit(Resource.fromResult(Source.CLOUD, data))
            }

        }.flowOn(Dispatchers.IO)
    }

    enum class CacheType(val type: String) {
        ForumGroups("forum_groups"),
        Threads("threads"),
        Posts("posts"),
    }
}