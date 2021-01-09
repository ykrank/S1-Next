package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.pref.ReadPreferencesManager
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.databinding.ItemThreadBinding
import me.ykrank.s1next.viewmodel.ThreadViewModel
import me.ykrank.s1next.viewmodel.UserViewModel
import javax.inject.Inject

class ThreadAdapterDelegate(context: Context, private val forumId: String) :
        BaseAdapterDelegate<Thread, SimpleRecycleViewHolder<ItemThreadBinding>>(context, Thread::class.java) {

    @Inject
    internal lateinit var mUserViewModel: UserViewModel

    @Inject
    internal lateinit var mThemeManager: ThemeManager

    @Inject
    internal lateinit var mReadPreferencesManager: ReadPreferencesManager

    init {
        App.appComponent.inject(this)
    }

    public override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemThreadBinding>(mLayoutInflater, R.layout.item_thread,
                parent, false)
        // we do not use view model for ThemeManager
        // because theme changes only when Activity recreated
        binding.userViewModel = mUserViewModel
        binding.themeManager = mThemeManager
        binding.forumId = forumId
        binding.model = ThreadViewModel()

        val threadPadding = mReadPreferencesManager.threadPadding
        if (threadPadding != null && threadPadding > 0) {
            val paddingPx = threadPadding * parent.context.resources.displayMetrics.scaledDensity.toInt()
            binding.tvThread.setPadding(binding.tvThread.paddingLeft, paddingPx, binding.tvThread.paddingLeft, paddingPx)
        }

        return SimpleRecycleViewHolder<ItemThreadBinding>(binding)
    }

    override fun onBindViewHolderData(t: Thread, position: Int, holder: SimpleRecycleViewHolder<ItemThreadBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.thread?.set(t)
    }

}
