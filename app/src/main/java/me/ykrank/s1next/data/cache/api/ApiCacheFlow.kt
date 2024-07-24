package me.ykrank.s1next.data.cache.api

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

class ApiCacheFlow(
    private val downloadPerf: DownloadPreferencesManager,
    private val cacheBiz: CacheBiz,
    private val user: User,
    private val jsonMapper: ObjectMapper,
) {

    fun getKey(type: ApiCacheConstants.CacheType, keys: List<Serializable?> = emptyList()): String {
        return "u${user.uid ?: ""}#${type.type}#${keys.joinToString(",") ?: ""}"
    }

    /**
     * 不指定CacheParam时，优先从网络获取
     */
    fun <T : Any> getFlow(
        type: ApiCacheConstants.CacheType,
        param: CacheParam?,
        cls: Class<T>,
        api: suspend () -> String,
        keys: List<Serializable?>,
        validator: ApiCacheValidator<T>? = null,
        loadTime: LoadTime = LoadTime(),
        printTime: Boolean = BuildConfig.DEBUG,
        group1: String? = null,
        group2: String? = null,
        group3: String? = null,
    ): Flow<Resource<T>> {
        val key = getKey(type, keys)
        val cacheStrategy = param?.strategy ?: CacheStrategy.NET_FIRST
        return flow {
            val cacheData: Cache? by lazy {
                loadTime.run(ApiCacheConstants.Time.TIME_LOAD_CACHE) {
                    cacheBiz.getTextZipByKey(key)
                }
            }

            fun parseCache(): T? {
                runCatching {
                    val json = cacheData?.decodeZipString
                    if (!json.isNullOrEmpty()) {
                        val data = loadTime.run(ApiCacheConstants.Time.TIME_PARSE_CACHE) {
                            jsonMapper.readValue(json, cls)
                        }
                        if (data != null && (validator == null || validator.getCacheValid(data))) {
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
                    L.i(
                        S1ApiCacheProvider.TAG,
                        "$key ${jsonMapper.writeValueAsString(loadTime.times)}"
                    )
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
                        printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_CACHE)
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
                loadTime.run(ApiCacheConstants.Time.TIME_NET) { api() }
                    .let {
                        val data = loadTime.run(ApiCacheConstants.Time.TIME_PARSE_NET) {
                            it.toJson(cls)
                        }
                        if (validator != null && !validator.getNetValid(data)) {
                            // 无效的数据降级到缓存
                            if (cacheFallbackEnable && !cacheFirst) {
                                parseCache()?.apply {
                                    fallbackSuccess = true
                                    printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_CACHE)
                                    emit(Resource.Success<T>(Source.PERSISTENCE, this))
                                }
                            }
                        }
                        if (downloadPerf.netCacheEnable &&
                            (validator == null || validator.setCacheValid(data))
                        ) {
                            // 有效的数据更新到缓存
                            withContext(Dispatchers.Default + L.report) {
                                loadTime.run(ApiCacheConstants.Time.TIME_SAVE_CACHE) {
                                    cacheBiz.saveZipAsync(
                                        key,
                                        user.uid?.toIntOrNull(),
                                        jsonMapper.writeValueAsString(data), // 这里不能在内部序列化，避免异步问题
                                        maxSize = downloadPerf.totalDataCacheSize,
                                        group = type.type,
                                        group1 = group1,
                                        group2 = group2,
                                        group3 = group3,
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
                        printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_CACHE)
                        emit(Resource.Success<T>(Source.PERSISTENCE, this))
                    }
                }
            }
            if (!fallbackSuccess) {
                printTimeWhenEmit(ApiCacheConstants.Time.TIME_EMIT_NET)
                emit(Resource.fromResult(Source.CLOUD, data))
            }
        }.flowOn(Dispatchers.IO)
    }
}