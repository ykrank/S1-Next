package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import io.reactivex.Single
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread

/**
 * A dialog requests to reply to post.
 */
class EditPostRequestDialogFragment : BaseRequestDialogFragment<String>() {

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<String> {
        val bundle = arguments!!
        val mThread = bundle.getParcelable<Thread>(ARG_THREAD)
        val mPost = bundle.getParcelable<Post>(ARG_POST)
        val title = bundle.getString(ARG_TITLE)
        val typeId = bundle.getString(ARG_TYPE_ID)
        val message = bundle.getString(ARG_MESSAGE)

        if (mPost == null || mThread == null) {
            return Single.error<String>(NullPointerException())
        }

        val saveAsDraft = if (BuildConfig.DEBUG && mPost.isFirst) 1 else null
        return flatMappedWithAuthenticityToken { token ->
            mS1Service.editPost(mThread.fid.toInt(), mThread.id.toInt(), mPost.id, token, System.currentTimeMillis(),
                    typeId, title, message, 1, 1, saveAsDraft)
        }
    }

    override fun onNext(data: String) {
        if (data.contains("succeedhandle_")) {
            onRequestSuccess(getString(R.string.edit_post_succeed))
        } else {
            onRequestError(data)
        }
    }

    companion object {

        val TAG: String = EditPostRequestDialogFragment::class.java.name

        private val ARG_THREAD = "thread"
        private val ARG_POST = "post"
        private val ARG_TYPE_ID = "type_id"
        private val ARG_TITLE = "title"
        private val ARG_MESSAGE = "message"

        fun newInstance(thread: Thread, post: Post, typeId: String?, title: String,
                        message: String): EditPostRequestDialogFragment {
            val fragment = EditPostRequestDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putParcelable(ARG_POST, post)
            bundle.putString(ARG_TYPE_ID, typeId)
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_MESSAGE, message)
            fragment.arguments = bundle

            return fragment
        }
    }
}
