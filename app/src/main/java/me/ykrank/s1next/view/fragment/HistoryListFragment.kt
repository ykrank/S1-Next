package me.ykrank.s1next.view.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.data.db.biz.HistoryBiz
import me.ykrank.s1next.databinding.FragmentBaseBinding
import me.ykrank.s1next.view.adapter.HistoryCursorRecyclerViewAdapter
import javax.inject.Inject

/**
 * Fragment show post view history list
 */
class HistoryListFragment : BaseFragment() {
    private var mRecyclerAdapter: HistoryCursorRecyclerViewAdapter? = null

    @Inject
    lateinit var historyBiz: HistoryBiz
    private lateinit var binding: FragmentBaseBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        leavePageMsg("HistoryListFragment")
        val activity: Activity = requireActivity()
        binding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        mRecyclerAdapter = HistoryCursorRecyclerViewAdapter(activity, viewLifecycleOwner)
        binding.recyclerView.setAdapter(mRecyclerAdapter)
    }

    override fun onPause() {
        mRecyclerAdapter?.changeCursor(null)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            val cursor = withContext(Dispatchers.IO) {
                historyBiz.getHistoryListCursor()
            }
            mRecyclerAdapter?.changeCursor(cursor)
        }
    }

    companion object {
        val TAG = HistoryListFragment::class.java.getName()

        @JvmStatic
        fun newInstance(): HistoryListFragment {
            return HistoryListFragment()
        }
    }
}
