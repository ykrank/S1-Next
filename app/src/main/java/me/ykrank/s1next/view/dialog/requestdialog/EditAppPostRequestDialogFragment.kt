package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import io.reactivex.Single
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppThread

/**
 * A dialog requests to reply to post.
 */
class EditAppPostRequestDialogFragment : BaseRequestDialogFragment<String>() {

    override fun getProgressMessage(): CharSequence? {
        return getText(R.string.dialog_progress_message_reply)
    }

    override fun getSourceObservable(): Single<String> {
        val bundle = arguments!!
        val mThread: AppThread = bundle.getParcelable(ARG_THREAD)
        val mPost: AppPost = bundle.getParcelable(ARG_POST)
        val title = bundle.getString(ARG_TITLE)
        val typeId = bundle.getString(ARG_TYPE_ID)
        val message = bundle.getString(ARG_MESSAGE)

        val saveAsDraft = if (BuildConfig.DEBUG && mPost.position == 1) 1 else null
        return flatMappedWithAuthenticityToken { token ->
            mS1Service.editPost(mThread.fid, mThread.tid, mPost.pid, token, System.currentTimeMillis(),
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

        val TAG: String = EditAppPostRequestDialogFragment::class.java.name

        private val ARG_THREAD = "thread"
        private val ARG_POST = "post"
        private val ARG_TYPE_ID = "type_id"
        private val ARG_TITLE = "title"
        private val ARG_MESSAGE = "message"

        fun newInstance(thread: AppThread, post: AppPost, typeId: String?, title: String,
                        message: String): EditAppPostRequestDialogFragment {
            val fragment = EditAppPostRequestDialogFragment()
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
