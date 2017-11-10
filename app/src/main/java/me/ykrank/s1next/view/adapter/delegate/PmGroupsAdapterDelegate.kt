package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.PmGroup
import me.ykrank.s1next.databinding.ItemPmGroupBinding
import me.ykrank.s1next.viewmodel.PmGroupViewModel
import javax.inject.Inject

class PmGroupsAdapterDelegate(context: Context) : BaseAdapterDelegate<PmGroup, PmGroupsAdapterDelegate.BindingViewHolder>(context, PmGroup::class.java) {

    @Inject
    lateinit var mRxBus: RxBus

    @Inject
    lateinit var mUser: User

    init {
        App.appComponent.inject(this)
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPmGroupBinding>(mLayoutInflater,
                R.layout.item_pm_group, parent, false)
        binding.model = PmGroupViewModel()
        binding.rxBus = mRxBus
        binding.user = mUser
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolderData(pmGroup: PmGroup, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.binding
        binding.model.pmGroup.set(pmGroup)
        binding.executePendingBindings()
    }

    class BindingViewHolder(val binding: ItemPmGroupBinding) : RecyclerView.ViewHolder(binding.root)
}
