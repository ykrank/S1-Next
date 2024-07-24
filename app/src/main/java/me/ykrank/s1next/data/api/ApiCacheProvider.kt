package me.ykrank.s1next.data.api

import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.Resource
import kotlinx.coroutines.flow.Flow
import me.ykrank.s1next.data.api.model.Rate
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */
interface ApiCacheProvider {
    /**
     * 板块列表
     */
    suspend fun getForumGroupsWrapper(
        param: CacheParam? = null
    ): Flow<Resource<ForumGroupsWrapper>>

    /**
     * 板块详情/主题列表
     */
    suspend fun getThreadsWrapper(
        forumId: String?,
        typeId: String?,
        page: Int,
        param: CacheParam? = null
    ): Flow<Resource<ThreadsWrapper>>

    /**
     * 主题详情/帖子列表
     */
    suspend fun getPostsWrapper(
        threadId: String?,
        page: Int,
        authorId: String? = null,
        ignoreCache: Boolean = false,
        onRateUpdate: ((pid: Int, rate: List<Rate>) -> Unit)? = null,
    ): Flow<Resource<PostsWrapper>>

    /**
     * 评分
     */
    suspend fun getPostRates(
        threadId: String?,
        postId: Int,
    ): Resource<List<Rate>>

}
