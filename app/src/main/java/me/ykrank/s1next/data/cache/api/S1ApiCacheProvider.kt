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
import me.ykrank.s1next.data.cache.CacheBiz
import me.ykrank.s1next.data.cache.Cache
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.util.toJson

class S1ApiCacheProvider(
    private val downloadPerf: DownloadPreferencesManager,
    private val s1Service: S1Service,
    private val cacheBiz: CacheBiz,
    user: User,
    private val jsonMapper: ObjectMapper,
) : ApiCacheProvider {

    private val apiCacheFlow = ApiCacheFlow(downloadPerf, cacheBiz, user, jsonMapper)

    override suspend fun getForumGroupsWrapper(param: CacheParam?): Flow<Resource<ForumGroupsWrapper>> {
        return apiCacheFlow.getFlow(
            ApiCacheConstants.CacheType.ForumGroups,
            param,
            ForumGroupsWrapper::class.java,
            api = {
                s1Service.getForumGroupsWrapper()
            },
            setValidator = {
                !it.data?.forumList.isNullOrEmpty()
            })
    }

    override suspend fun getThreadsWrapper(
        forumId: String?,
        typeId: String?,
        page: Int,
        param: CacheParam?
    ): Flow<Resource<ThreadsWrapper>> {
        return apiCacheFlow.getFlow(
            ApiCacheConstants.CacheType.Threads,
            param,
            ThreadsWrapper::class.java,
            api = {
                s1Service.getThreadsWrapper(forumId, typeId, page)
            },
            setValidator = {
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
        return apiCacheFlow.getFlow(
            ApiCacheConstants.CacheType.Posts,
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
                                loadTime.run(ApiCacheConstants.Time.TIME_SAVE_CACHE) {
                                    cacheBiz.saveTextZipAsync(
                                        apiCacheFlow.getKey(
                                            ApiCacheConstants.CacheType.Posts,
                                            param
                                        ),
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
                    loadTime.addPoint(ApiCacheConstants.Time.TIME_LOAD_END)
                    L.i(TAG, "posts:$threadId ${jsonMapper.writeValueAsString(loadTime.times)}")
                }
            }
    }



    companion object {
        const val TAG = "S1ApiCache"
    }
}