package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.Pm
import me.ykrank.s1next.databinding.ItemPmLeftBinding
import me.ykrank.s1next.viewmodel.PmViewModel
import javax.inject.Inject

class PmLeftAdapterDelegate(context: Context) : BaseAdapterDelegate<Pm, SimpleRecycleViewHolder<ItemPmLeftBinding>>(context, Pm::class.java) {

    @Inject
    internal lateinit var user: User

    init {
        App.appComponent.inject(this)
    }

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean {
        val item = items[position]
        return if (item is Pm) {
            !TextUtils.equals(item.authorId, user.uid)
        } else false
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPmLeftBinding>(mLayoutInflater,
                R.layout.item_pm_left, parent, false)
        binding.pmViewModel = PmViewModel()
        return SimpleRecycleViewHolder<ItemPmLeftBinding>(binding)
    }

    override fun onBindViewHolderData(t: Pm, position: Int, holder: SimpleRecycleViewHolder<ItemPmLeftBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.pmViewModel?.pm?.set(t)
    }

    /**
     * make textview selectable
     *
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val binding = (holder as SimpleRecycleViewHolder<ItemPmLeftBinding>).binding
        binding.tvMessage.isEnabled = false
        binding.tvMessage.isEnabled = true
    }

}
