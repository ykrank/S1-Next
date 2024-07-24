package me.ykrank.s1next.data.cache.api

import androidx.annotation.WorkerThread
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.CacheStrategy
import com.github.ykrank.androidtools.data.Resource
import com.github.ykrank.androidtools.data.Source
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.LoadTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.cache.biz.CacheBiz
import me.ykrank.s1next.data.cache.dbmodel.Cache
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.util.toJson
import java.io.Serializable

open class ApiCacheFlow<T>(
    private val downloadPerf: DownloadPreferencesManager,
    private val cacheBiz: CacheBiz,
    private val user: User,
    private val jsonMapper: ObjectMapper,
    /**
     * 也会作为group存在数据库
     */
    private val type: ApiCacheConstants.CacheType,
    /**
     * 缓存策略
     */
    private val param: CacheParam?,
    /**
     * 数据类型
     */
    private val cls: Class<T>,
    /**
     * 获取网络数据
     */
    private val api: suspend () -> String,
    /**
     * 和user,type一起组装，作为缓存的唯一标识
     */
    private val keys: List<Serializable?>,
    /**
     * 对数据进行后处理
     */
    private val interceptor: ApiCacheInterceptor<T> = ApiCacheInterceptorPass(),
    /**
     * 跟踪加载时间
     */
    private val loadTime: LoadTime = LoadTime(),
    /**
     * 日志中打印加载时间
     */
    private val printTime: Boolean = BuildConfig.DEBUG,
    /**
     * 作为额外group存在数据库
     */
    private val groupsExtra: List<String> = emptyList(),
) {
    private val key = getKey(type, keys)

    private val cacheStrategy = param?.strategy ?: CacheStrategy.NET_FIRST

    @get:WorkerThread
    private val cacheData: Cache? by lazy {
        loadTime.run(ApiCacheConstants.Time.TIME_LOAD_CACHE) {
            getCache()
        }
    }

    @get:WorkerThread
    private val decodeCacheData: T? by lazy {
        parseCache()
    }

    fun getKey(type: ApiCacheConstants.CacheType, keys: List<Serializable?> = emptyList()): String {
        return Companion.getKey(user, type, keys)
    }

    @WorkerThread
    open fun getCache(): Cache? {
        return cacheBiz.getTextZipByKey(key)
    }

    @WorkerThread
    fun parseCache(): T? {
        runCatching {
            val json = cacheData?.decodeZipString
            if (!json.isNullOrEmpty()) {
                val data = loadTime.run(ApiCacheConstants.Time.TIME_PARSE_CACHE) {
                    jsonMapper.readValue(json, cls)
                }
                if (data != null) {
                    return interceptor.interceptQueryCache(data)
                }
            }
        }.onFailure {
            L.report(it)
        }
        return null
    }

    @WorkerThread
    fun printTimeWhenEmit(name: String) {
        loadTime.addPoint(name)
        if (printTime) {
            L.i(
                S1ApiCacheProvider.TAG,
                "$key ${jsonMapper.writeValueAsString(loadTime.times)}"
            )
        }
    }

    @WorkerThread
    fun getCacheResource(): Resource.Success<T>? {
        val expired =
            System.currentTimeMillis() - (cacheData?.timestamp
                ?: 0) > cacheStrategy.strategy.expired.inWholeMilliseconds
        if (!expired) {
            decodeCacheData?.apply {
                printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_CACHE)
                return Resource.Success<T>(Source.PERSISTENCE, this)
            }
        }
        return null
    }

    @WorkerThread
    fun getCacheResourceFallback(): Resource.Success<T>? {
        val expired =
            System.currentTimeMillis() - (cacheData?.timestamp
                ?: 0) > cacheStrategy.fallbackStrategy.expired.inWholeMilliseconds
        if (!expired) {
            decodeCacheData?.apply {
                printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_CACHE)
                return Resource.Success<T>(Source.PERSISTENCE, this)
            }
        }
        return null
    }

    /**
     * 不指定CacheParam时，优先从网络获取
     */
    fun getFlow(): Flow<Resource<T>> {
        val cacheStrategy = param?.strategy ?: CacheStrategy.NET_FIRST
        return flow {
            // 优先拉取缓存
            val cacheFirst = downloadPerf.netCacheEnable && !cacheStrategy.strategy.ignoreCache
            val cacheFallback =
                downloadPerf.netCacheEnable && !cacheStrategy.fallbackStrategy.ignoreCache

            if (cacheFirst) {
                getCacheResource()?.apply {
                    emit(this)
                }
            }

            // 拉取网络数据
            var fallbackSuccess = false
            val data = runCatching {
                loadTime.run(ApiCacheConstants.Time.TIME_NET) { api() }
                    .let {
                        val data = loadTime.run(ApiCacheConstants.Time.TIME_PARSE_NET) {
                            it.toJson(cls)
                        }
                        if (interceptor.shouldNetDataFallback(data)) {
                            // 无效的数据降级到缓存
                            if (!cacheFirst && cacheFallback) {
                                getCacheResourceFallback()?.apply {
                                    fallbackSuccess = true
                                    emit(this)
                                }
                            }
                        }
                        if (downloadPerf.netCacheEnable) {
                            val postData = interceptor.interceptSaveCache(data)
                            if (postData != null) {
                                // 有效的数据更新到缓存
                                withContext(Dispatchers.Default + L.report) {
                                    loadTime.run(ApiCacheConstants.Time.TIME_SAVE_CACHE) {
                                        cacheBiz.saveZipAsync(
                                            key,
                                            user.uid?.toIntOrNull(),
                                            jsonMapper.writeValueAsString(postData), // 这里不能在内部序列化，避免异步问题
                                            maxSize = downloadPerf.totalDataCacheSize,
                                            groups = listOf(type.type) + groupsExtra,
                                        )
                                    }
                                }
                            }
                        }
                        data
                    }
            }.onFailure {
                // 失败的请求降级到缓存
                if (!cacheFirst && cacheFallback) {
                    getCacheResourceFallback()?.apply {
                        fallbackSuccess = true
                        emit(this)
                    }
                }
            }
            if (!fallbackSuccess) {
                printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_NET)
                emit(Resource.fromResult<T>(Source.CLOUD, data))
            }
        }.flowOn(Dispatchers.IO)
    }

    companion object {
        fun getKey(
            user: User,
            type: ApiCacheConstants.CacheType,
            keys: List<Serializable?> = emptyList()
        ): String {
            return "u${user.uid ?: ""}#${type.type}#${keys.joinToString(",")}"
        }
    }
}