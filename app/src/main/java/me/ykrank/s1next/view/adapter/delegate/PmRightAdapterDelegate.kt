package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.text.TextUtils
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.Pm
import me.ykrank.s1next.databinding.ItemPmRightBinding
import me.ykrank.s1next.viewmodel.PmViewModel
import javax.inject.Inject

class PmRightAdapterDelegate(context: Context, private val lifecycleOwner: LifecycleOwner) : BaseAdapterDelegate<Pm, SimpleRecycleViewHolder<ItemPmRightBinding>>(context, Pm::class.java) {

    @Inject
    internal lateinit var user: User

    init {
        App.appComponent.inject(this)
    }

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean {
        val item = items[position]
        return if (item is Pm) {
            TextUtils.equals(item.authorId, user.uid)
        } else false
    }

    public override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPmRightBinding>(mLayoutInflater,
                R.layout.item_pm_right, parent, false)
        binding.pmViewModel = PmViewModel(lifecycleOwner)
        return SimpleRecycleViewHolder<ItemPmRightBinding>(binding)
    }

    override fun onBindViewHolderData(t: Pm, position: Int, holder: SimpleRecycleViewHolder<ItemPmRightBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.pmViewModel?.pm?.set(t)
    }

    /**
     * make textview selectable
     *
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val binding = (holder as SimpleRecycleViewHolder<ItemPmRightBinding>).binding
        binding.tvMessage.isEnabled = false
        binding.tvMessage.isEnabled = true
    }

}
