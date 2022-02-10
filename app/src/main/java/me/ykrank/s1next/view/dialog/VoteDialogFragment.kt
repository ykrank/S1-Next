package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.ui.adapter.simple.BindViewHolderCallback
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.rxkotlin.Singles
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.databinding.ItemVoteBinding
import me.ykrank.s1next.databinding.LayoutVoteBinding
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.viewmodel.ItemVoteViewModel
import me.ykrank.s1next.viewmodel.VoteViewModel
import javax.inject.Inject


/**
 * A dialog lets the user vote thread.
 */
class VoteDialogFragment : BaseDialogFragment(), VoteViewModel.VoteVmAction {
    @Inject
    lateinit var appService: AppService

    @Inject
    lateinit var s1Service: S1Service

    @Inject
    lateinit var mUser: User

    private lateinit var tid: String
    private lateinit var mVote: Vote
    private lateinit var binding: LayoutVoteBinding
    private lateinit var adapter: SimpleRecycleViewAdapter

    private lateinit var data: List<ItemVoteViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        tid = arguments!!.getString(ARG_THREAD_ID)!!
        mVote = arguments!!.getParcelable(ARG_VOTE)!!

        adapter = SimpleRecycleViewAdapter(context!!, R.layout.item_vote, false, BindViewHolderCallback { position, itemBind ->
            itemBind as ItemVoteBinding
            itemBind.radio.setOnClickListener { refreshSelectedItem(position, itemBind) }
            itemBind.checkBox.setOnClickListener { refreshSelectedItem(position, itemBind) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutVoteBinding.inflate(inflater, container, false)

        val model = VoteViewModel(mVote, this)
        binding.model = model

        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mVote.voteOptions?.let {
            data = it.values.map { ItemVoteViewModel(binding.model!!, it) }
            adapter.swapDataSet(data)
        }

        loadData()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun loadData() {
        Singles.zip(appService.getPollInfo(mUser.appSecureToken, tid), appService.getPollOptions(mUser.appSecureToken, tid))
                .compose(ApiFlatTransformer.apiPairErrorTransformer())
                .compose(RxJavaUtil.iOSingleTransformer())
                .map { (a,b)-> a.data to b.data }
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ (appVote, voteOptions)->
                    binding.model?.appVote?.set(appVote)
                    voteOptions?.let {
                        data.forEachIndexed { index, vm ->
                            vm.option.mergeWithAppVoteOption(it[index], appVote?.voters
                                    ?: mVote.voteCount)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }, {
                    val activity = activity ?: return@subscribe
                    activity.toast(ErrorUtil.parse(activity, it))
                })
    }

    private fun refreshSelectedItem(position: Int, itemBind: ItemVoteBinding) {
        if (mVote.isMultiple) {
            itemBind.model?.let {
                if (it.selected.get()) {
                    it.selected.set(false)
                } else {
                    val selected = data.filter { it.selected.get() }
                    if (selected.size < mVote.maxChoices) {
                        it.selected.set(true)
                    }
                }
            }
        } else {
            data.forEachIndexed { index, vm -> vm.selected.set(index == position) }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onClickVote(view: View?) {
        val selected = data.filter { it.selected.get() }.map { it.option.optionId }
        s1Service.vote(tid, mUser.authenticityToken, selected)
                .compose(ApiFlatTransformer.apiErrorTransformer())
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({
                    activity?.toast(R.string.vote_success)
                    loadData()
                }, {
                    val activity = activity ?: return@subscribe
                    activity.toast(ErrorUtil.parse(activity, it))
                })
    }

    companion object {

        const val ARG_VOTE = "vote"
        const val ARG_THREAD_ID = "thread_id"
        val TAG: String = VoteDialogFragment::class.java.name

        fun newInstance(threadId: String, vote: Vote): VoteDialogFragment {
            val fragment = VoteDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_THREAD_ID, threadId)
            bundle.putParcelable(ARG_VOTE, vote)
            fragment.arguments = bundle

            return fragment
        }
    }
}
