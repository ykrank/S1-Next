package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.databinding.ItemThreadBinding
import me.ykrank.s1next.viewmodel.ThreadViewModel
import me.ykrank.s1next.viewmodel.UserViewModel
import javax.inject.Inject

class ThreadAdapterDelegate(context: Context, private val forumId: String) : BaseAdapterDelegate<Thread, ThreadAdapterDelegate.BindingViewHolder>(context, Thread::class.java) {

    @Inject
    internal lateinit var mUserViewModel: UserViewModel

    @Inject
    internal lateinit var mThemeManager: ThemeManager

    init {

        App.appComponent.inject(this)
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemThreadBinding>(mLayoutInflater, R.layout.item_thread,
                parent, false)
        // we do not use view model for ThemeManager
        // because theme changes only when Activity recreated
        binding.userViewModel = mUserViewModel
        binding.themeManager = mThemeManager
        binding.forumId = forumId
        binding.model = ThreadViewModel()

        return BindingViewHolder(binding)
    }

    override fun onBindViewHolderData(thread: Thread, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.itemThreadBinding
        binding.model.thread.set(thread)
        binding.executePendingBindings()
    }

    class BindingViewHolder(internal val itemThreadBinding: ItemThreadBinding) : RecyclerView.ViewHolder(itemThreadBinding.root)
}
