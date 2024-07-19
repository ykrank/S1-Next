package me.ykrank.s1next.data.api.empty

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.CacheStrategy
import com.github.ykrank.androidtools.data.Resource
import com.github.ykrank.androidtools.data.Source
import com.github.ykrank.androidtools.util.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import me.ykrank.s1next.BuildConfig
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
        val loadTime = LoadTime()
        val ratePostFlow = flow {
            val rates = runCatching {
                loadTime.run("get_posts_new") {
                    s1Service.getPostsWrapperNew(threadId, page, authorId).let {
                        jsonMapper.readValue(it, RatePostsWrapper::class.java)
                    }
                    }
                }
            emit(rates)
        }.flowOn(Dispatchers.IO)
        return getFlow(
                CacheType.Posts,
                param,
                PostsWrapper::class.java,
                loadTime = loadTime,
                printTime = false,
                api = {
                    s1Service.getPostsWrapper(threadId, page, authorId)
                }, setValidator = {
                    // 需要后处理才能更新缓存
                    false
                })
            .combine(ratePostFlow) { it, ratePostWrapper ->
                if (it.source.isCloud()) {
                    withContext(Dispatchers.IO) {
                        var hasError = false
                        val postWrapper = it.data
                        ratePostWrapper.apply {
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
                                    loadTime.run("get_post_trade_info") {
                                        s1Service.getTradePostInfo(threadId, post.id + 1)
                                    }.apply {
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
                            withContext(Dispatchers.Default) {
                                loadTime.run(TIME_SAVE_CACHE) {
                                    cacheBiz.saveTextZipAsync(
                                        getKey(CacheType.Posts, param),
                                        jsonMapper.writeValueAsString(postWrapper),
                                        maxSize = downloadPerf.totalDataCacheSize
                                    )
                                }
                            }
                        }
                    }
                }
                it
            }.onEach {
                    if (BuildConfig.DEBUG) {
                        loadTime.addPoint(TIME_LOAD_END)
                        L.i(TAG, "posts:$threadId ${jsonMapper.writeValueAsString(loadTime.times)}")
                    }
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
        loadTime: LoadTime = LoadTime(),
        printTime: Boolean = BuildConfig.DEBUG,
    ): Flow<Resource<T>> {
        val key = getKey(type, param)
        val cacheStrategy = param?.strategy ?: CacheStrategy.NET_FIRST
        return flow {
            val cacheData: Cache? by lazy {
                loadTime.run(TIME_LOAD_CACHE) {
                    cacheBiz.getTextZipByKey(key)
                }
            }

            fun parseCache(): T? {
                runCatching {
                    val json = cacheData?.text
                    if (!json.isNullOrEmpty()) {
                        val data = loadTime.run(TIME_PARSE_CACHE) {
                            jsonMapper.readValue(json, cls)
                        }
                        if (data != null && (getValidator == null || getValidator(data))) {
                            return data
                        }
                    }
                }.onFailure {
                    L.report(it)
                }
                return null
            }

            fun printTimeWhenEmit(name: String) {
                loadTime.addPoint(name)
                if (printTime) {
                    L.i(TAG, "$key ${jsonMapper.writeValueAsString(loadTime.times)}")
                }
            }

            // 优先拉取缓存
            val cacheFirst = downloadPerf.netCacheEnable && !cacheStrategy.strategy.ignoreCache
            if (cacheFirst) {
                val expired =
                        System.currentTimeMillis() - (cacheData?.timestamp
                            ?: 0) > cacheStrategy.strategy.expired.inWholeMilliseconds
                if (!expired) {
                    parseCache()?.apply {
                        printTimeWhenEmit(TIME_EMIT_CACHE)
                        emit(Resource.Success<T>(Source.PERSISTENCE, this))
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
                loadTime.run(TIME_NET) { api() }
                    .let {
                        val data = loadTime.run(TIME_PARSE_NET) {
                            it.toJson(cls)
                        }
                        if (getValidator != null && !getValidator(data)) {
                            // 无效的数据降级到缓存
                            if (cacheFallbackEnable && !cacheFirst) {
                                parseCache()?.apply {
                                    fallbackSuccess = true
                                    printTimeWhenEmit(TIME_EMIT_CACHE)
                                    emit(Resource.Success<T>(Source.PERSISTENCE, this))
                                }
                            }
                        }
                        if (setValidator == null || setValidator(data)) {
                            // 有效的数据更新到缓存
                            withContext(Dispatchers.Default) {
                                loadTime.run(TIME_SAVE_CACHE) {
                                    cacheBiz.saveTextZipAsync(
                                        key,
                                        jsonMapper.writeValueAsString(data),
                                        maxSize = downloadPerf.totalDataCacheSize
                                    )
                                }
                            }
                        }
                        data
                }
            }.onFailure {
                // 失败的请求降级到缓存
                if (cacheFallbackEnable && !cacheFirst) {
                    parseCache()?.apply {
                        fallbackSuccess = true
                        printTimeWhenEmit(TIME_EMIT_CACHE)
                        emit(Resource.Success<T>(Source.PERSISTENCE, this))
                    }
                }
            }
            if (!fallbackSuccess) {
                printTimeWhenEmit(TIME_EMIT_NET)
                emit(Resource.fromResult(Source.CLOUD, data))
            }
        }.flowOn(Dispatchers.IO)
    }

    enum class CacheType(val type: String) {
        ForumGroups("forum_groups"),
        Threads("threads"),
        Posts("posts"),
    }

    class LoadTime() {
        private val start = System.currentTimeMillis()
        private val timeMap = mutableMapOf<String, Long>()

        val times: Map<String, Long>
            get() = timeMap.mapValues {
                it.value - start
            }

        fun addPoint(name: String) {
            timeMap[name] = System.currentTimeMillis()
        }

        fun start(name: String) {
            timeMap[name + "_start"] = System.currentTimeMillis()
        }

        fun end(name: String) {
            timeMap[name + "_end"] = System.currentTimeMillis()
        }

        inline fun <T> run(name: String, block: () -> T): T {
            start(name)
            return try {
                block()
            } catch (e: Throwable) {
                throw e
            } finally {
                end(name)
            }
        }
    }

    companion object {
        const val TAG = "S1ApiCache"
        const val TIME_LOAD_END = "load_end"
        const val TIME_LOAD_CACHE = "load_cache"
        const val TIME_SAVE_CACHE = "save_cache"
        const val TIME_PARSE_CACHE = "parse_cache"
        const val TIME_NET = "NET"
        const val TIME_PARSE_NET = "parse_net"
        const val TIME_EMIT_CACHE = "emit_cache"
        const val TIME_EMIT_NET = "emit_net"
    }
}