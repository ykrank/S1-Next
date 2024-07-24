package me.ykrank.s1next.view.page.post.prefetch

import android.os.Bundle
import android.view.View
import androidx.annotation.MainThread
import androidx.lifecycle.lifecycleScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper
import me.ykrank.s1next.data.cache.api.ApiCacheConstants
import me.ykrank.s1next.data.cache.api.ApiCacheFlow
import me.ykrank.s1next.data.cache.biz.CacheBiz
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.dialog.BaseLoadProgressDialogFragment
import javax.inject.Inject
import kotlin.math.max


/**
 * A dialog lets user load website blacklist.
 */
class ThreadPrefetchDialogFragment : BaseLoadProgressDialogFragment() {
    @Inject
    lateinit var mUser: User

    @Inject
    lateinit var apiCache: ApiCacheProvider

    @Inject
    lateinit var cacheBiz: CacheBiz

    @Inject
    lateinit var jsonMapper: ObjectMapper

    private lateinit var threadId: String
    private var pageStart: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        threadId = requireArguments().getString(ARG_THREAD_ID)!!
        pageStart = requireArguments().getInt(ARG_PAGE_START, 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNextPage(pageStart)
    }

    private fun updateProgress(page: Int, max: Int) {
        if (max > 0) {
            binding.max = max
        }
        binding.progress = page
    }

    @MainThread
    private fun loadNextPage(page: Int) {
        lifecycleScope.launch {
            val cache = withContext(Dispatchers.IO + L.report) {
                val key = ApiCacheFlow.getKey(
                    mUser,
                    ApiCacheConstants.CacheType.Posts,
                    listOf(threadId, page)
                )
                cacheBiz.getTextZipByKey(key)?.decodeZipString?.let {
                    jsonMapper.readValue(it, PostsWrapper::class.java)
                }
            }
            val thread = cache?.data?.postListInfo
            if (thread != null && page < thread.pageCount) {
                // 已预加载，而且非最后一页的数据，不用重新拉取
                loadNextPage(page + 1)
                updateProgress(page, max(binding.max, thread.pageCount))
                return@launch
            }
            apiCache.getPostsWrapper(
                threadId,
                page,
                ignoreCache = true
            ).onCompletion {
                val max = binding.max
                if (max > 0) {
                    if (max > page) {
                        loadNextPage(page + 1)
                    } else {
                        delay(2000)
                        this@ThreadPrefetchDialogFragment.dismiss()
                    }
                }
            }.collect {
                if (it.data != null) {
                    val max = it.data?.data?.postListInfo?.pageCount ?: 0
                    updateProgress(page, max)
                } else {
                    requireActivity().apply {
                        toast(ErrorUtil.parse(this, it.error))
                    }
                }
            }
        }
    }

    companion object {
        val TAG: String = ThreadPrefetchDialogFragment::class.java.simpleName
        const val ARG_THREAD_ID = "thread_id"
        const val ARG_PAGE_START = "page_start"

        fun newInstance(threadId: String, page: Int?): ThreadPrefetchDialogFragment {
            val fragment = ThreadPrefetchDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_THREAD_ID, threadId)
                putInt(ARG_PAGE_START, page ?: 1)
            }
            return fragment
        }
    }
}
