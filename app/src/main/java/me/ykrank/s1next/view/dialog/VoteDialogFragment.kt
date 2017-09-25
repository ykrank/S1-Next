package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.databinding.LayoutVoteBinding
import me.ykrank.s1next.view.adapter.simple.SimpleRecycleViewAdapter
import me.ykrank.s1next.viewmodel.VoteViewModel


/**
 * A dialog lets the user vote thread.
 */
class VoteDialogFragment : BaseDialogFragment() {

    private lateinit var mVote: Vote
    private lateinit var binding: LayoutVoteBinding
    private lateinit var adapter: SimpleRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mVote = arguments.getParcelable(TAG_VOTE)
        adapter = SimpleRecycleViewAdapter(context, R.layout.item_vote)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutVoteBinding.inflate(inflater, container, false)

        val model = VoteViewModel()
        model.vote.set(mVote)
        binding.model = model

        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)
        mVote.voteOptions?.let {
            adapter.swapDataSet(it.values.toList())
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {

        val TAG_VOTE = "vote"
        val TAG: String = VoteDialogFragment::class.java.name

        fun newInstance(vote: Vote): VoteDialogFragment {
            val fragment = VoteDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(TAG_VOTE, vote)
            fragment.arguments = bundle

            return fragment
        }
    }
}
