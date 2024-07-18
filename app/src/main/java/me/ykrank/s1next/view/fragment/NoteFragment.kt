package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import com.github.ykrank.androidtools.util.MathUtil
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.collection.Notes
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper
import me.ykrank.s1next.databinding.FragmentNoteBinding
import me.ykrank.s1next.view.activity.WebViewActivity
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter
import me.ykrank.s1next.view.adapter.NoteRecyclerViewAdapter
import me.ykrank.s1next.view.event.NoticeRefreshEvent
import javax.inject.Inject

/**
 * Created by ykrank on 2017/1/5.
 */

class NoteFragment : BaseLoadMoreRecycleViewFragment<BaseDataWrapper<Notes>>() {
    private lateinit var mRecyclerAdapter: NoteRecyclerViewAdapter

    @Inject
    internal lateinit var mRxBus: RxBus

    override val isCardViewContainer: Boolean
        get() = true

    override val recyclerViewAdapter: BaseRecyclerViewAdapter
        get() = mRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        leavePageMsg("NoteFragment")

        val recyclerView = recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerAdapter = NoteRecyclerViewAdapter(activity)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun getLoadingViewModelBindingDelegateImpl(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LoadingViewModelBindingDelegateNoteImpl {
        val binding = FragmentNoteBinding.inflate(inflater, container, false)

        binding.tvHint.setOnClickListener { WebViewActivity.start(requireContext(), Api.URL_VIEW_NOTE, enableJS = true, pcAgent = true) }
        return LoadingViewModelBindingDelegateNoteImpl(binding)
    }

    override fun appendNewData(oldData: BaseDataWrapper<Notes>?, newData: BaseDataWrapper<Notes>): BaseDataWrapper<Notes> {
        if (oldData != null) {
            val oldNotes = oldData.data?.list
            var newNotes = newData.data?.list?.toMutableList()
            if (newNotes == null) {
                newNotes = ArrayList()
                newData.data?.list = newNotes
            }
            if (oldNotes != null) {
                newNotes.addAll(0, oldNotes)
            }
        }
        return newData
    }

    override fun getPageSourceObservable(pageNum: Int): Single<BaseDataWrapper<Notes>> {
        return mS1Service.getMyNotes(pageNum)
    }

    override fun onNext(data: BaseDataWrapper<Notes>) {
        super.onNext(data)
        val notes = data.data
        val noteList = notes?.list
        if (notes != null && noteList != null) {
            mRecyclerAdapter.diffNewDataSet(noteList, false)
            //update total page
            setTotalPages(MathUtil.divide(notes.count, notes.perPage))
        }

        if (pageNum == 1) {
            mRxBus.post(NoticeRefreshEvent::class.java, NoticeRefreshEvent(null, false))
        }
    }

    companion object {
        val TAG = NoteFragment::class.java.name

        fun newInstance(): NoteFragment {
            return NoteFragment()
        }
    }
}

class LoadingViewModelBindingDelegateNoteImpl(
    private val binding: FragmentNoteBinding
) : LoadingViewModelBindingDelegate {

    override val rootView: View
        get() = binding.root
    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout
    override val recyclerView: RecyclerView
        get() = binding.recyclerView

    override val hintView: TextView
        get() = binding.tvHint

    override fun setLoadingViewModel(loadingViewModel: LoadingViewModel) {
        binding.loadingViewModel = loadingViewModel
    }
}
