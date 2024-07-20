package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.search.ForumSearchResult
import me.ykrank.s1next.databinding.ItemSearchForumBinding

class SearchForumAdapterDelegate(context: Context) :
    BaseAdapterDelegate<ForumSearchResult, SimpleRecycleViewHolder<ItemSearchForumBinding>>(
        context,
        ForumSearchResult::class.java
    ) {

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSearchForumBinding>(
            mLayoutInflater,
            R.layout.item_search_forum, parent, false
        )

        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolderData(
        t: ForumSearchResult,
        position: Int,
        holder: SimpleRecycleViewHolder<ItemSearchForumBinding>,
        payloads: List<Any>
    ) {
        val binding = holder.binding
        binding.model = t
    }
}
