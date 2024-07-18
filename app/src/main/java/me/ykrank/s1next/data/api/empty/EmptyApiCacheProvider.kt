package me.ykrank.s1next.data.api.empty

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.CacheStrategy
import com.github.ykrank.androidtools.data.Resource
import com.github.ykrank.androidtools.data.Source
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper
import me.ykrank.s1next.data.db.biz.CacheBiz
import me.ykrank.s1next.data.db.dbmodel.Cache
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.util.toJson

class EmptyApiCacheProvider(
    private val downloadPerf: DownloadPreferencesManager,
    private val s1Service: S1Service,
    private val cacheBiz: CacheBiz,
    private val user: User,
    private val jsonMapper: ObjectMapper,
) : ApiCacheProvider {

    override suspend fun getForumGroupsWrapper(param: CacheParam?): Flow<Resource<ForumGroupsWrapper>> {
        return getFlow(CacheType.ForumGroups, param, ForumGroupsWrapper::class.java, api = {
            s1Service.getForumGroupsWrapper()
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
        })
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

    /**
     * 不指定CacheParam时，优先从网络获取
     */
    private fun <T : Any> getFlow(
        type: CacheType,
        param: CacheParam?,
        cls: Class<T>,
        api: suspend () -> String,
        validator: ((data: T) -> Boolean)? = null,
    ): Flow<Resource<T>> {
        val key = "u${user.uid ?: 0}#${type.type}#${param?.keys?.joinToString(",") ?: ""}"
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
                        (validator == null || validator(data))
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
                    if (validator == null || validator(data)) {
                        // 有效的数据更新到缓存
//                        cacheBiz.saveTextZip(key, it, maxSize = downloadPerf.totalDataCacheSize)
                        cacheBiz.saveTextZip(
                            key,
                            jsonMapper.writeValueAsString(data),
                            maxSize = downloadPerf.totalDataCacheSize
                        )
                    } else {
                        // 无效的数据降级到缓存
                        if (cacheFallbackEnable) {
                            parseCache()?.apply {
                                fallbackSuccess = true
                                emit(Resource.Success<T>(Source.PERSISTENCE, this))
                            }
                        }
                    }
                    data
                }
            }.onFailure {
                // 失败的请求降级到缓存
                if (cacheFallbackEnable) {
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
    }
}