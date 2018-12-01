package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.databinding.ItemForumBinding
import me.ykrank.s1next.viewmodel.ForumViewModel
import javax.inject.Inject

class ForumAdapterDelegate(context: Context) : BaseAdapterDelegate<Forum, SimpleRecycleViewHolder<ItemForumBinding>>(context, Forum::class.java) {
    @Inject
    internal lateinit var themeManager: ThemeManager

    init {
        App.appComponent.inject(this)
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemForumBinding>(mLayoutInflater,
                R.layout.item_forum, parent, false)
        binding.gentleAccentColor = themeManager.gentleAccentColor
        binding.forumViewModel = ForumViewModel()

        return SimpleRecycleViewHolder<ItemForumBinding>(binding)
    }

    override fun onBindViewHolderData(t: Forum, position: Int, holder: SimpleRecycleViewHolder<ItemForumBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.forumViewModel?.forum?.set(t)
    }

}
