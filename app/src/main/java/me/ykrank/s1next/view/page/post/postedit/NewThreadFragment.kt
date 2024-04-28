package me.ykrank.s1next.view.page.post.postedit

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.ThreadType
import me.ykrank.s1next.data.cache.NewThreadCacheModel
import me.ykrank.s1next.databinding.FragmentNewThreadBinding
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter
import me.ykrank.s1next.view.dialog.requestdialog.NewThreadRequestDialogFragment
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent
import javax.inject.Inject

/**
 * A Fragment shows [EditText] to let the user edit thread.
 */
class NewThreadFragment : BasePostEditFragment() {
    @Inject
    internal lateinit var mS1Service: S1Service
    @Inject
    internal lateinit var objectMapper: ObjectMapper
    private var mCacheKey: String? = null
    private var mForumId: Int = 0

    private lateinit var titleEditText: EditText
    private lateinit var typeSpinner: Spinner

    private var cacheModel: NewThreadCacheModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newThreadBinding = DataBindingUtil.inflate<FragmentNewThreadBinding>(inflater, R.layout.fragment_new_thread, container, false)
        initCreateView(newThreadBinding.layoutPost!!)
        titleEditText = newThreadBinding.title
        typeSpinner = newThreadBinding.spinner
        return newThreadBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mForumId = arguments!!.getInt(ARG_FORUM_ID)
        mCacheKey = String.format(CACHE_KEY_PREFIX, mForumId)
        leavePageMsg("NewThreadFragment##mForumId:" + mForumId)

        App.appComponent.inject(this)
        init()
    }

    override fun onMenuSendClick(): Boolean {
        var typeId: String? = null
        if (typeSpinner.visibility == View.VISIBLE) {
            val selectType = typeSpinner.selectedItem as ThreadType?
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

        val title = titleEditText.text.toString()
        val message = mReplyView.text.toString()
        if (!isTitleValid(title) || !isMessageValid(message)) {
            showShortSnackbar(R.string.error_no_title_or_message)
            return true
        }

        NewThreadRequestDialogFragment.newInstance(mForumId, typeId, title, message, cacheKey)
                .show(fragmentManager!!, NewThreadRequestDialogFragment.TAG)

        return true
    }

    override val cacheKey: String?
        get() = mCacheKey

    override fun isRequestDialogAccept(event: RequestDialogSuccessEvent): Boolean {
        return event.dialogFragment is NewThreadRequestDialogFragment
    }

    override fun isContentEmpty(): Boolean {
        return super.isContentEmpty() && titleEditText.text.isNullOrBlank()
    }

    override fun buildCacheString(): String? {
        val model = NewThreadCacheModel()
        model.selectPosition = typeSpinner.selectedItemPosition
        model.title = titleEditText.text.toString()
        model.message = mReplyView.text.toString()
        try {
            return objectMapper.writeValueAsString(model)
        } catch (e: JsonProcessingException) {
            L.report(e)
        }

        return super.buildCacheString()
    }

    override fun resumeFromCache(cache: Single<String>): Disposable {
        return cache.map { s -> objectMapper.readValue(s, NewThreadCacheModel::class.java) }
                .compose(RxJavaUtil.iOSingleTransformer<NewThreadCacheModel>())
                .subscribe({ model ->
                    cacheModel = model
                    titleEditText.setText(model.title)
                    mReplyView.setText(model.message)
                }, L::report)
    }

    private fun isTitleValid(string: String): Boolean {
        if (string.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun isMessageValid(string: String): Boolean {
        return isTitleValid(string)
    }

    private fun init() {
        mS1Service.getNewThreadInfo(mForumId)
                .map<List<ThreadType>>(ThreadType.Companion::fromXmlString)
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({
                    if (it.isEmpty()) {
                        showRetrySnackbar(getString(R.string.message_network_error), View.OnClickListener { v -> init() })
                    } else {
                        setSpinner(it)
                    }
                }, {
                    L.report(it)
                    showRetrySnackbar(it, View.OnClickListener { v -> init() })
                })
    }

    private fun setSpinner(types: List<ThreadType>) {
        if (types.isEmpty()) {
            typeSpinner.visibility = View.GONE
            return
        } else {
            typeSpinner.visibility = View.VISIBLE
        }
        val spinnerAdapter = SimpleSpinnerAdapter(context!!, types) { it?.typeName.toString() }
        typeSpinner.adapter = spinnerAdapter
        if (cacheModel != null && types.size > cacheModel!!.selectPosition) {
            typeSpinner.setSelection(cacheModel!!.selectPosition)
        }
    }

    companion object {
        val TAG: String = NewThreadFragment::class.java.name

        private const val ARG_FORUM_ID = "forum_id"

        private val CACHE_KEY_PREFIX = "NewThread_%s"

        fun newInstance(forumId: Int): NewThreadFragment {
            val fragment = NewThreadFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_FORUM_ID, forumId)
            fragment.arguments = bundle
            return fragment
        }
    }

}
