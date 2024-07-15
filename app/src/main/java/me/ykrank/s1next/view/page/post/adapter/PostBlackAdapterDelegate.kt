package me.ykrank.s1next.view.page.post.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.ykrank.androidlifecycle.AndroidLifeCycle
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.ItemPostBlackBinding
import me.ykrank.s1next.view.adapter.delegate.BaseAdapterDelegate
import me.ykrank.s1next.view.page.post.viewmodel.PostBlackViewModel
import me.ykrank.s1next.widget.span.FixedSpannableFactory
import me.ykrank.s1next.widget.span.PostMovementMethod
import javax.inject.Inject

class PostBlackAdapterDelegate(private val fragment: Fragment, context: Context) :
    BaseAdapterDelegate<Post, SimpleRecycleViewHolder<ItemPostBlackBinding>>(
        context,
        Post::class.java
    ) {

    @Inject
    internal lateinit var mRxBus: RxBus

    @Inject
    internal lateinit var mS1Service: S1Service

    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager
    private var threadInfo: Thread? = null
    private var voteInfo: Vote? = null
    private var pageNum: Int = 1

    init {
        App.appComponent.inject(this)
    }

    private fun setTextSelectable(binding: ItemPostBlackBinding, selectable: Boolean) {
        binding.authorName.setTextIsSelectable(selectable)

        binding.tvReply.setTextIsSelectable(selectable)
        binding.tvReply.movementMethod = PostMovementMethod.getInstance()
    }

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean {
        val item = items[position]
        return item is Post && item.hide != Post.HIDE_NO
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPostBlackBinding>(
            mLayoutInflater,
            R.layout.item_post_black, parent, false
        )
        binding.postViewModel = PostBlackViewModel(fragment.viewLifecycleOwner, mRxBus)

        binding.tvReply.setSpannableFactory(FixedSpannableFactory())

        //Bind textview lifecycle to fragment
        AndroidLifeCycle.bindFragment(binding.tvReply, fragment)

        //If setTextIsSelectable, then should reset movement
        val selectable = mGeneralPreferencesManager.isPostSelectable
        setTextSelectable(binding, selectable)

        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolderData(
        post: Post,
        position: Int,
        holder: SimpleRecycleViewHolder<ItemPostBlackBinding>,
        payloads: List<Any>
    ) {
        val binding = holder.binding

        binding.quickSidebarEnable = mGeneralPreferencesManager.isQuickSideBarEnable

        val selectable = mGeneralPreferencesManager.isPostSelectable
        if (selectable != binding.tvReply.isTextSelectable) {
            setTextSelectable(binding, selectable)
        }

        binding.postViewModel?.let {
            it.thread.set(threadInfo)
            it.pageNum.set(pageNum)
            it.post.set(post)

            if ("1" == post.number) {
                it.vote.set(voteInfo)
            } else {
                it.vote.set(null)
            }
        }

        binding.executePendingBindings()
    }

    // Bug workaround for losing text selection ability, see:
    // https://code.google.com/p/android/issues/detail?id=208169
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (mGeneralPreferencesManager.isPostSelectable) {
            val binding = (holder as SimpleRecycleViewHolder<ItemPostBlackBinding>).binding
            binding.authorName.isEnabled = false
            binding.tvReply.isEnabled = false
            binding.authorName.isEnabled = true
            binding.tvReply.isEnabled = true
        }
    }

    fun setThreadInfo(threadInfo: Thread, pageNum: Int) {
        this.threadInfo = threadInfo
        this.pageNum = pageNum
    }

    fun setVoteInfo(voteInfo: Vote?) {
        this.voteInfo = voteInfo
    }

}

