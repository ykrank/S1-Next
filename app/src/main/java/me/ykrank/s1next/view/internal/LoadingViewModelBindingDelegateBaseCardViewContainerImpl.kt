package me.ykrank.s1next.view.internal

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ykrank.androidtools.ui.internal.LoadingViewModelBindingDelegate
import com.github.ykrank.androidtools.ui.vm.LoadingViewModel
import me.ykrank.s1next.databinding.FragmentBaseCardViewContainerBinding

class LoadingViewModelBindingDelegateBaseCardViewContainerImpl(
    private val binding: FragmentBaseCardViewContainerBinding
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
        binding.setLoadingViewModel(loadingViewModel)
    }
}
