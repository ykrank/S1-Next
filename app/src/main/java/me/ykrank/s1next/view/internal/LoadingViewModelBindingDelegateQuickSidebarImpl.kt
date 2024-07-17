package me.ykrank.s1next.view.internal

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import me.ykrank.s1next.databinding.FragmentBaseWithQuickSideBarBinding

class LoadingViewModelBindingDelegateQuickSidebarImpl<D>(
    private val binding: FragmentBaseWithQuickSideBarBinding
) : LoadingViewModelBindingDelegate<D> {
    override val rootView: View
        get() = binding.root
    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout
    override val recyclerView: RecyclerView
        get() = binding.recyclerView

    override fun setLoadingViewModel(loadingViewModel: LoadingViewModel<D>) {
        binding.setLoadingViewModel(loadingViewModel)
    }
}
