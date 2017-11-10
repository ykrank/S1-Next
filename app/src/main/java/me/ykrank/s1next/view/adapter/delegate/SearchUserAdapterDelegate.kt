package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.search.UserSearchResult
import me.ykrank.s1next.databinding.ItemSearchUserBinding
import me.ykrank.s1next.viewmodel.SearchUserViewModel

class SearchUserAdapterDelegate(context: Context) : BaseAdapterDelegate<UserSearchResult, SimpleRecycleViewHolder<ItemSearchUserBinding>>(context, UserSearchResult::class.java) {

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSearchUserBinding>(mLayoutInflater,
                R.layout.item_search_user, parent, false)
        binding.model = SearchUserViewModel()
        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolderData(userSearchResult: UserSearchResult, position: Int, holder: SimpleRecycleViewHolder<ItemSearchUserBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model.search.set(userSearchResult)
        binding.executePendingBindings()
    }
}
