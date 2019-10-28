package me.ykrank.s1next.view.fragment

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.ui.adapter.simple.BindViewHolderCallback
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.util.RxJavaUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.RatePreInfo
import me.ykrank.s1next.databinding.FragmentNewRateBinding
import me.ykrank.s1next.databinding.ItemRateReasonBinding
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter
import me.ykrank.s1next.view.dialog.requestdialog.RateRequestDialogFragment
import me.ykrank.s1next.viewmodel.NewRateViewModel
import javax.inject.Inject

/**
 * A Fragment shows [EditText] to let the user enter reply.
 */
class NewRateFragment : BaseFragment() {

    @Inject
    internal lateinit var s1Service: S1Service

    private var threadId: String? = null
    private var postID: String? = null
    private var ratePreInfo: RatePreInfo? = null

    private lateinit var binding: FragmentNewRateBinding
    private lateinit var reasonAdapter: SimpleRecycleViewAdapter
    private lateinit var bindViewHolderCallback: BindViewHolderCallback

    private val score: String
        get() = binding.spinner.selectedItem as String

    private val reason: String
        get() = binding.etReason.text.toString()

    private val isScoreValid: Boolean
        get() {
            val score = score
            return !(TextUtils.isEmpty(score) || "0" == score.trim { it <= ' ' })
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_rate, container, false)
        binding.model = NewRateViewModel()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        threadId = arguments?.getString(ARG_THREAD_ID)
        postID = arguments?.getString(ARG_POST_ID)
        leavePageMsg("NewRateFragment##threadId:$threadId,postID:$postID")

        App.appComponent.inject(this)
        init()
        refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_new_rate, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send -> {
                postRate()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        bindViewHolderCallback = BindViewHolderCallback { position, bind ->
            val itemBinding = bind as ItemRateReasonBinding
            itemBinding.root.setOnClickListener { v -> binding.etReason.setText(itemBinding.model) }
        }

        binding.recycleView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        reasonAdapter = SimpleRecycleViewAdapter(context!!, R.layout.item_rate_reason, false, bindViewHolderCallback, null)
        binding.recycleView.adapter = reasonAdapter
    }

    private fun refreshData() {
        reasonAdapter.setHasProgress(true)

        s1Service.getRatePreInfo(threadId, postID, System.currentTimeMillis())
                .map<RatePreInfo> { RatePreInfo.fromHtml(it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ info ->
                    ratePreInfo = info
                    binding.model?.info?.set(info)
                    setSpinner(info.scoreChoices)
                    setReasonRecycleView(info.reasons)
                }, { e ->
                    reasonAdapter.setHasProgress(false)
                    showRetrySnackbar(e, View.OnClickListener { v -> refreshData() })
                }
                )
    }

    private fun setSpinner(choices: List<String>) {
        val spinnerAdapter = SimpleSpinnerAdapter(context!!, choices) { it.toString() }
        binding.spinner.adapter = spinnerAdapter
    }

    private fun setReasonRecycleView(reasons: List<String>) {
        reasonAdapter.refreshDataSet(reasons, false)
    }

    private fun postRate() {
        val ratePreInfo = ratePreInfo
        if (ratePreInfo == null) {
            showShortSnackbar(R.string.error_not_init)
            return
        }
        if (!isScoreValid) {
            showShortSnackbar(R.string.invalid_score)
            return
        }
        RateRequestDialogFragment.newInstance(ratePreInfo, score, reason).show(fragmentManager!!,
                RateRequestDialogFragment.TAG)
    }

    companion object {

        val TAG = NewRateFragment::class.java.name

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_POST_ID = "post_id"

        fun newInstance(threadId: String, postId: String): NewRateFragment {
            val fragment = NewRateFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putString(ARG_POST_ID, postId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
