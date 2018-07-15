package me.ykrank.s1next.view.adapter.delegate

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.ItemAppPostBinding
import me.ykrank.s1next.viewmodel.AppPostViewModel
import me.ykrank.s1next.widget.span.PostMovementMethod
import javax.inject.Inject

class AppPostAdapterDelegate(activity: Activity, private val quotePid: String?) : BaseAdapterDelegate<AppPost, SimpleRecycleViewHolder<ItemAppPostBinding>>(activity, AppPost::class.java) {

    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mUser: User
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    private var threadInfo: AppThread? = null

    init {
        App.appComponent.inject(this)
    }

    private fun setTextSelectable(binding: ItemAppPostBinding, selectable: Boolean) {
        binding.authorName.setTextIsSelectable(selectable)
        binding.tvFloor.setTextIsSelectable(selectable)
        binding.tvReply.setTextIsSelectable(selectable)
        binding.authorName.movementMethod = LinkMovementMethod.getInstance()
        binding.tvFloor.movementMethod = LinkMovementMethod.getInstance()
        binding.tvReply.movementMethod = PostMovementMethod.getInstance()
        binding.tvFloor.isLongClickable = false
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAppPostBinding>(mLayoutInflater,
                R.layout.item_app_post, parent, false)
        binding.postViewModel = AppPostViewModel(mRxBus, mUser)

        //If setTextIsSelectable, then should reset movement
        val selectable = mGeneralPreferencesManager.isPostSelectable
        setTextSelectable(binding, selectable)

        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolderData(post: AppPost, position: Int, holder: SimpleRecycleViewHolder<ItemAppPostBinding>, payloads: List<Any>) {
        val binding = holder.binding

        val selectable = mGeneralPreferencesManager.isPostSelectable
        if (selectable != binding.tvReply.isTextSelectable) {
            setTextSelectable(binding, selectable)
        }

        binding.postViewModel?.let {
            it.thread.set(threadInfo)
            it.post.set(post)
        }
        val quote = post.pid == quotePid?.toInt()
        if (quote) {
            binding.container.setBackgroundResource(R.drawable.shape_stroke_corners_wide)
        } else {
            binding.container.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.executePendingBindings()
    }

    // Bug workaround for losing text selection ability, see:
    // https://code.google.com/p/android/issues/detail?id=208169
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (mGeneralPreferencesManager!!.isPostSelectable) {
            val binding = (holder as SimpleRecycleViewHolder<ItemAppPostBinding>).binding
            binding.authorName.isEnabled = false
            binding.tvFloor.isEnabled = false
            binding.tvReply.isEnabled = false
            binding.authorName.isEnabled = true
            binding.tvFloor.isEnabled = true
            binding.tvReply.isEnabled = true
        }
    }

    fun setThreadInfo(threadInfo: AppThread) {
        this.threadInfo = threadInfo
    }
}
