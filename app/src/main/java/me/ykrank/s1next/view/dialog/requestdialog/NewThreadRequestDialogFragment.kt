package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import io.reactivex.Single
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper

/**
 * A dialog requests to reply to post.
 */
class NewThreadRequestDialogFragment : BaseRequestDialogFragment<AccountResultWrapper>() {
    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<AccountResultWrapper> {
        val bundle = arguments
        val forumId = bundle!!.getInt(ARG_FORUM_ID)
        val title = bundle.getString(ARG_TITLE)
        val typeId = bundle.getString(ARG_TYPE_ID)
        val message = bundle.getString(ARG_MESSAGE)
        val saveAsDraft = if (BuildConfig.DEBUG) 1 else null
        return flatMappedWithAuthenticityToken { token: String? ->
            mS1Service.newThread(
                forumId,
                token,
                System.currentTimeMillis(),
                typeId,
                title,
                message,
                1,
                1,
                saveAsDraft
            )
        }
    }

    protected override fun onNext(data: AccountResultWrapper) {
        val result = data.result
        if (result.status == STATUS_NEW_THREAD_SUCCESS) {
            onRequestSuccess(result.message)
        } else {
            onRequestError(result.message)
        }
    }

    companion object {
        val TAG = NewThreadRequestDialogFragment::class.java.getName()
        private const val ARG_FORUM_ID = "forum_id"
        private const val ARG_TYPE_ID = "type_id"
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_CACHE_KEY = "cache_key"
        private const val STATUS_NEW_THREAD_SUCCESS = "post_newthread_succeed"
        fun newInstance(
            forumId: Int, typeId: String?, title: String?,
            message: String?, cacheKey: String?
        ): NewThreadRequestDialogFragment {
            val fragment = NewThreadRequestDialogFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_FORUM_ID, forumId)
            bundle.putString(ARG_TYPE_ID, typeId)
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_MESSAGE, message)
            bundle.putString(ARG_CACHE_KEY, cacheKey)
            fragment.setArguments(bundle)
            return fragment
        }
    }
}
