package me.ykrank.s1next.view.page.post.prefetch

import android.os.Bundle
import android.view.View
import androidx.annotation.MainThread
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.extension.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.dialog.BaseLoadProgressDialogFragment
import javax.inject.Inject


/**
 * A dialog lets user load website blacklist.
 */
class ThreadPrefetchDialogFragment : BaseLoadProgressDialogFragment() {
    @Inject
    lateinit var mUser: User

    @Inject
    lateinit var apiCache: ApiCacheProvider

    private lateinit var threadId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        threadId = requireArguments().getString(ARG_THREAD_ID)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNextPage()
    }

    @MainThread
    private fun loadNextPage(page: Int = 1) {
        lifecycleScope.launch {
            apiCache.getPostsWrapper(
                threadId,
                page,
                param = CacheParam(true)
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
                    if (max > 0) {
                        binding.max = max
                    }
                    binding.progress = page
                } else {
                    requireActivity().apply {
                        toast(ErrorUtil.parse(this, it.error))
                    }
                }
            }
        }
    }

    companion object {
        val TAG: String = ThreadPrefetchDialogFragment::class.java.name
        const val ARG_THREAD_ID = "thread_id"

        fun newInstance(threadId: String): ThreadPrefetchDialogFragment {
            val fragment = ThreadPrefetchDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_THREAD_ID, threadId)
            }
            return fragment
        }
    }
}
