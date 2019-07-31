package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.darkroom.DarkRoom
import me.ykrank.s1next.databinding.ItemDarkRoomBinding
import me.ykrank.s1next.viewmodel.DarkRoomViewModel

class DarkRoomAdapterDelegate(context: Context) : BaseAdapterDelegate<DarkRoom, SimpleRecycleViewHolder<ItemDarkRoomBinding>>(context, DarkRoom::class.java) {

    public override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDarkRoomBinding>(mLayoutInflater,
                R.layout.item_dark_room, parent, false)
        binding.model = DarkRoomViewModel()
        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolderData(t: DarkRoom, position: Int, holder: SimpleRecycleViewHolder<ItemDarkRoomBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.darkRoom?.set(t)
    }

}
