package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.api.model.PostEditor
import me.ykrank.s1next.data.api.model.ThreadType
import me.ykrank.s1next.databinding.FragmentEditPostBinding
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.util.L
import me.ykrank.s1next.util.RxJavaUtil
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter
import me.ykrank.s1next.view.dialog.requestdialog.EditAppPostRequestDialogFragment
import me.ykrank.s1next.view.dialog.requestdialog.EditPostRequestDialogFragment
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent
import javax.inject.Inject

/**
 * A Fragment shows [EditText] to let the user enter reply.
 */
class EditAppPostFragment : BasePostFragment() {

    @Inject
    internal lateinit var mS1Service: S1Service

    private lateinit var mThread: AppThread
    private lateinit var mPost: AppPost
    private var isHost: Boolean = false

    private lateinit var binding: FragmentEditPostBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        initCreateView(binding.layoutPost)

        mThread = arguments.getParcelable(ARG_THREAD)
        mPost = arguments.getParcelable(ARG_POST)

        isHost = mPost.position == 1
        binding.host = isHost
        L.leaveMsg(String.format("EditAppPostFragment##post:%s", mPost))

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        App.getAppComponent().inject(this)
        init()
    }

    override fun OnMenuSendClick(): Boolean {
        var typeId: String? = null
        if (binding.spinner.visibility == View.VISIBLE) {
            val selectType = binding.spinner.selectedItem as ThreadType?
            if (selectType == null) {
                showShortSnackbar(R.string.error_not_init)
                return true
            }
            typeId = selectType.typeId
            //未选择类别
            if (typeId == null || "0" == typeId.trim { it <= ' ' }) {
                showShortSnackbar(R.string.error_no_type_id)
                return true
            }
        }

        val title = binding.title.text.toString()
        val message = mReplyView.text.toString()
        if (!isTitleValid(title) || !isMessageValid(message)) {
            showShortSnackbar(R.string.error_no_title_or_message)
            return true
        }

        EditAppPostRequestDialogFragment.newInstance(mThread, mPost, typeId, title, message)
                .show(fragmentManager, EditPostRequestDialogFragment.TAG)

        return true
    }

    override val cacheKey: String?
        get() = null

    private fun isTitleValid(string: String): Boolean {
        if (!isHost) {
            return true
        }
        if (string.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun isMessageValid(string: String): Boolean {
        if (string.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun init() {
        mS1Service.getEditPostInfo(mThread.fid, mThread.tid, mPost.pid)
                .map<PostEditor>({ PostEditor.fromHtml(it) })
                .compose(RxJavaUtil.iOTransformer<PostEditor>())
                .to(AndroidRxDispose.withObservable<PostEditor>(this, FragmentEvent.DESTROY))
                .subscribe({ postEditor ->
                    if (isHost) {
                        setSpinner(postEditor.threadTypes)
                        binding.spinner.setSelection(postEditor.typeIndex)
                        binding.title.setText(postEditor.subject)
                    }
                    binding.layoutPost.reply.setText(postEditor.message)
                }, { e ->
                    L.report(e)
                    showRetrySnackbar(ErrorUtil.parse(context, e)) { init() }
                })

    }

    private fun setSpinner(types: List<ThreadType>?) {
        if (types == null || types.isEmpty()) {
            binding.spinner.visibility = View.GONE
            return
        } else {
            binding.spinner.visibility = View.VISIBLE
        }
        val spinnerAdapter = SimpleSpinnerAdapter(context, types, { it.typeName })
        binding.spinner.adapter = spinnerAdapter
    }

    override fun isRequestDialogAccept(event: RequestDialogSuccessEvent): Boolean {
        return false
    }

    companion object {

        val TAG: String = EditAppPostFragment::class.java.name

        private val ARG_THREAD = "thread"
        private val ARG_POST = "post"

        fun newInstance(thread: AppThread, post: AppPost): EditAppPostFragment {
            val fragment = EditAppPostFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putParcelable(ARG_POST, post)
            fragment.arguments = bundle
            return fragment
        }
    }
}
