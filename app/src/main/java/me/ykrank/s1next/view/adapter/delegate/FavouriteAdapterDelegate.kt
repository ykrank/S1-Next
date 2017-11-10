package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Favourite
import me.ykrank.s1next.databinding.ItemFavouriteBinding
import me.ykrank.s1next.viewmodel.FavouriteViewModel
import javax.inject.Inject

class FavouriteAdapterDelegate(context: Context) : BaseAdapterDelegate<Favourite, FavouriteAdapterDelegate.BindingViewHolder>(context, Favourite::class.java) {

    @Inject
    lateinit var mRxBus: RxBus

    init {
        App.appComponent.inject(this)
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemFavouriteBinding>(mLayoutInflater,
                R.layout.item_favourite, parent, false)
        binding.model = FavouriteViewModel()
        binding.rxBus = mRxBus
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolderData(favourite: Favourite, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.itemFavouriteBinding
        binding.model.favourite.set(favourite)
        binding.executePendingBindings()
    }

    class BindingViewHolder(val itemFavouriteBinding: ItemFavouriteBinding) : RecyclerView.ViewHolder(itemFavouriteBinding.root)
}
