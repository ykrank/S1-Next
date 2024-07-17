package me.ykrank.s1next.data.api.empty

import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.Resource
import com.github.ykrank.androidtools.data.Source
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.data.pref.DownloadPreferencesManager

class EmptyApiCacheProvider(
    private val downloadPerf: DownloadPreferencesManager,
    private val s1Service: S1Service
) : ApiCacheProvider {
    override fun getForumGroupsWrapper(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<String> {
        return oWrapper
    }

    override suspend fun getForumGroupsWrapper(param: CacheParam?): Flow<Resource<ForumGroupsWrapper>> {
        val wrapper = s1Service.getForumGroupsWrapper()
        return flowOf(Resource.Success(Source.CLOUD, wrapper))
    }

    override fun getThreadsWrapper(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<String> {
        return oWrapper
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
}