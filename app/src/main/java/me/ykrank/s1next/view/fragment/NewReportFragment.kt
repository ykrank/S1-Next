package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.databinding.ProgressBarMenuBinding
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.ApiException
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.ReportPreInfo
import me.ykrank.s1next.databinding.FragmentNewReportBinding
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * A Fragment shows [EditText] to let the user enter reply.
 */
class NewReportFragment : BaseFragment() {

    @Inject
    internal lateinit var s1Service: S1Service

    private var threadId: String? = null
    private var postID: String? = null
    private var pageNum = 1
    private var reportPreInfo: ReportPreInfo? = null

    private var sendMenu: MenuItem? = null
    private var sendMenuIcon: Drawable? = null
    private var sendMenuView: View? = null
    private var menuProgressBinding: ProgressBarMenuBinding? = null

    private lateinit var binding: FragmentNewReportBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_report, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        threadId = arguments?.getString(ARG_THREAD_ID)
        postID = arguments?.getString(ARG_POST_ID)
        pageNum = arguments?.getInt(ARG_PAGE_NUM, 1) ?: 1
        L.leaveMsg("NewReportFragment##threadId:$threadId,postID:$postID,pageNum:$pageNum")

        App.appComponent.inject(this)
        init()
        refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_new_report, menu)
        sendMenu = menu?.findItem(R.id.menu_send)
        sendMenuIcon = sendMenu?.icon
        sendMenuView = sendMenu?.actionView
        menuProgressBinding = ProgressBarMenuBinding.inflate(layoutInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send -> {
                sendReport()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {

    }

    private fun refreshData() {
        s1Service.getReportPreInfo(threadId, postID, System.currentTimeMillis())
                .map<ReportPreInfo> { ReportPreInfo.fromHtml(threadId, pageNum, it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ info ->
                    reportPreInfo = info
                    setSpinner(info.reason)
                }, {
                    showRetrySnackbar(it, View.OnClickListener { v -> refreshData() })
                }
                )
    }

    private fun setSpinner(reason: List<String>) {
        val spinnerAdapter = SimpleSpinnerAdapter(context!!, reason) { it.toString() }
        binding.spinnerReason.adapter = spinnerAdapter
    }

    private fun sendReport() {
        val preInfo = reportPreInfo
        if (preInfo == null || preInfo.fields.isEmpty()) {
            showShortSnackbar(R.string.error_not_init)
            return
        }
        val msg = binding.etMsg.text?.toString()
        if (msg.isNullOrEmpty()) {
            showShortSnackbar(R.string.error_reason_required)
            return
        }
        val reason = binding.spinnerReason.selectedItem as String?

        sendMenu?.actionView = menuProgressBinding?.root
        sendMenu?.isEnabled = false
        s1Service.report(preInfo.fields, reason, msg)
                .compose(RxJavaUtil.iOSingleTransformer())
                .doAfterTerminate {
                    sendMenu?.actionView = sendMenuView
                    sendMenu?.isEnabled = true
                }
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({
                    val pattern = Pattern.compile("errorhandle_\\('(.+)'")
                    val matcher = pattern.matcher(it)
                    val msg: String
                    if (matcher.find()) {
                        msg = matcher.group(1)
                    } else {
                        msg = it
                    }
                    if (msg?.contains("举报成功") == true) {
                        showShortTextAndFinishCurrentActivity("举报成功")
                    } else {
                        L.report(ApiException.ApiServerException(msg))
                        showShortSnackbar(msg)
                    }
                }, {
                    L.report(it)
                    showShortSnackbar(it)
                })
    }

    companion object {

        val TAG = NewReportFragment::class.java.name

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_POST_ID = "post_id"
        private val ARG_PAGE_NUM = "page_num"

        fun newInstance(threadId: String, postId: String, pageNum: Int): NewReportFragment {
            val fragment = NewReportFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putString(ARG_POST_ID, postId)
            bundle.putInt(ARG_PAGE_NUM, pageNum)
            fragment.arguments = bundle
            return fragment
        }
    }
}
