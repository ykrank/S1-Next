package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import io.reactivex.Single
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.AjaxResult
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread

/**
 * A dialog requests to reply to post.
 */
class EditPostRequestDialogFragment : BaseRequestDialogFragment<AjaxResult>() {

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<AjaxResult> {
        val bundle = requireArguments()
        val mThread = bundle.getParcelable<Thread>(ARG_THREAD)
        val mPost = bundle.getParcelable<Post>(ARG_POST)
        val title = bundle.getString(ARG_TITLE)
        val typeId = bundle.getString(ARG_TYPE_ID)
        val readPerm = bundle.getString(ARG_READ_PERM)
        val message = bundle.getString(ARG_MESSAGE)

        if (mPost == null || mThread == null) {
            return Single.error(NullPointerException())
        }

        val saveAsDraft = if (BuildConfig.DEBUG && mPost.isFirst) 1 else null
        return flatMappedWithAuthenticityToken { token ->
            mS1Service.editPost(mThread.fid!!.toInt(), mThread.id!!.toInt(), mPost.id, token, System.currentTimeMillis(),
                typeId,
                title,
                message,
                1,
                1,
                saveAsDraft,
                readPerm
            ).map {
                AjaxResult.fromAjaxString(it)
            }
        }
    }

    override fun onNext(data: AjaxResult) {
        if (data.success) {
            onRequestSuccess(getString(R.string.edit_post_succeed))
        } else {
            onRequestError(data.msg)
        }
    }

    companion object {

        val TAG: String = EditPostRequestDialogFragment::class.java.simpleName

        private const val ARG_THREAD = "thread"
        private const val ARG_POST = "post"
        private const val ARG_TYPE_ID = "type_id"
        private const val ARG_READ_PERM = "read_perm"
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun newInstance(thread: Thread, post: Post, typeId: String?, readPerm: String?, title: String,
                        message: String): EditPostRequestDialogFragment {
            val fragment = EditPostRequestDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putParcelable(ARG_POST, post)
            bundle.putString(ARG_TYPE_ID, typeId)
            bundle.putString(ARG_READ_PERM, readPerm)
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_MESSAGE, message)
            fragment.arguments = bundle

            return fragment
        }
    }
}
