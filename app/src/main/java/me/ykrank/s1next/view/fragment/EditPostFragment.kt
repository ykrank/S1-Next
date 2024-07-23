package me.ykrank.s1next.view.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.PostEditor
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.ThreadType
import me.ykrank.s1next.databinding.FragmentEditPostBinding
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter
import me.ykrank.s1next.view.dialog.requestdialog.EditPostRequestDialogFragment
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent
import me.ykrank.s1next.view.page.post.postedit.BasePostEditFragment
import javax.inject.Inject

/**
 * A Fragment shows [EditText] to let the user enter reply.
 */
class EditPostFragment : BasePostEditFragment() {

    @Inject
    internal lateinit var mS1Service: S1Service

    private lateinit var mThread: Thread
    private lateinit var mPost: Post
    private var isHost: Boolean = false

    private lateinit var binding: FragmentEditPostBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        initCreateView(binding.layoutPost)

        mThread = requireArguments().getParcelable(ARG_THREAD)!!
        mPost = requireArguments().getParcelable(ARG_POST)!!

        isHost = mPost.isFirst
        binding.host = isHost
        leavePageMsg(String.format("EditPostFragment##post:%s", mPost))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    override fun onMenuSendClick(): Boolean {
        var typeId: String? = null
        if (binding.spinnerType.visibility == View.VISIBLE) {
            val selectType = binding.spinnerType.selectedItem as ThreadType?
            if (selectType == null) {
                showSnackbar(R.string.error_not_init)
                return true
            }
            typeId = selectType.typeId
            //未选择类别
            if (typeId == null || "0" == typeId.trim()) {
                showSnackbar(R.string.error_no_type_id)
                return true
            }
        }
        var readPerm: String? = null
        if (binding.spinnerPerm.visibility == View.VISIBLE) {
            readPerm = binding.spinnerPerm.selectedItem as String?
        }

        val title = binding.title.text?.toString()
        val message = mReplyView.text?.toString()
        if (!isTitleValid(title) || !isMessageValid(message)) {
            showSnackbar(R.string.error_no_title_or_message)
            return true
        }

        EditPostRequestDialogFragment.newInstance(mThread, mPost, typeId, readPerm, title!!, message!!)
                .show(parentFragmentManager, EditPostRequestDialogFragment.TAG)

        return true
    }

    override val cacheKey: String?
        get() = null

    private fun isTitleValid(string: String?): Boolean {
        if (!isHost) {
            return true
        }
        if (string.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun isMessageValid(string: String?): Boolean {
        if (string.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun init() {
        mS1Service.getEditPostInfo(mThread.fid!!.toInt(), mThread.id!!.toInt(), mPost.id)
                .map<PostEditor> { PostEditor.fromHtml(it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ postEditor ->
                    if (isHost) {
                        setSpinnerType(postEditor.threadTypes)
                        binding.spinnerType.setSelection(postEditor.typeIndex)

                        setSpinnerPerm(postEditor.readPermTypes)
                        binding.spinnerPerm.setSelection(postEditor.readPermIndex)

                        binding.title.setText(postEditor.subject)
                    }
                    binding.layoutPost.reply.setText(postEditor.message)
                }, { e ->
                    L.report(e)
                    showRetrySnackbar(e, View.OnClickListener { init() })
                })

    }

    private fun setSpinnerType(types: List<ThreadType>?) {
        if (types.isNullOrEmpty()) {
            binding.spinnerType.visibility = View.GONE
            return
        } else {
            binding.spinnerType.visibility = View.VISIBLE
        }
        val spinnerAdapter = SimpleSpinnerAdapter(requireContext(), types) { it?.typeName.toString() }
        binding.spinnerType.adapter = spinnerAdapter
    }

    private fun setSpinnerPerm(types: List<String>?) {
        if (types.isNullOrEmpty()) {
            binding.spinnerPerm.visibility = View.GONE
            return
        } else {
            binding.spinnerPerm.visibility = View.VISIBLE
        }
        val spinnerAdapter = SimpleSpinnerAdapter(requireContext(), types) { it.toString() }
        binding.spinnerPerm.adapter = spinnerAdapter
    }

    override fun isRequestDialogAccept(event: RequestDialogSuccessEvent): Boolean {
        return event.dialogFragment is EditPostRequestDialogFragment
    }

    override fun onRequestDialogSuccess() {
        activity?.setResult(Activity.RESULT_OK)
    }

    companion object {

        val TAG: String = EditPostFragment::class.java.simpleName

        private const val ARG_THREAD = "thread"
        private const val ARG_POST = "post"

        fun newInstance(thread: Thread, post: Post): EditPostFragment {
            val fragment = EditPostFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_THREAD, thread)
            bundle.putParcelable(ARG_POST, post)
            fragment.arguments = bundle
            return fragment
        }
    }
}
