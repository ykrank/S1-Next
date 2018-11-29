package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.ReportPreInfo
import me.ykrank.s1next.databinding.FragmentNewReportBinding
import me.ykrank.s1next.util.ErrorUtil
import javax.inject.Inject

/**
 * A Fragment shows [EditText] to let the user enter reply.
 */
class NewReportFragment : BaseFragment() {

    @Inject
    internal lateinit var s1Service: S1Service

    private var threadId: String? = null
    private var postID: String? = null

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
        L.leaveMsg("NewReportFragment##threadId:$threadId,postID:$postID")

        App.appComponent.inject(this)
        init()
        refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_new_rate, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send -> {

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {

    }

    private fun refreshData() {
        s1Service.getReportPreInfo(threadId, postID, System.currentTimeMillis())
                .map<ReportPreInfo> { ReportPreInfo.fromHtml(it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ info ->
                    L.d(App.preAppComponent.jsonMapper.writeValueAsString(info))
                }, { e ->
                    showRetrySnackbar(ErrorUtil.parse(context!!, e), View.OnClickListener { v -> refreshData() })
                }
                )
    }

    companion object {

        val TAG = NewReportFragment::class.java.name

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_POST_ID = "post_id"

        fun newInstance(threadId: String, postId: String): NewReportFragment {
            val fragment = NewReportFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putString(ARG_POST_ID, postId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
