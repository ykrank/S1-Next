package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import com.github.ykrank.androidlifecycle.AndroidLifeCycle
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.ItemPostBinding
import me.ykrank.s1next.viewmodel.PostViewModel
import me.ykrank.s1next.widget.span.PostMovementMethod
import javax.inject.Inject

class PostAdapterDelegate(private val fragment: Fragment, context:Context) : BaseAdapterDelegate<Post, PostAdapterDelegate.ItemViewBindingHolder>(context, Post::class.java) {

    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mUser: User
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager
    private var threadInfo: Thread? = null
    private var voteInfo: Vote? = null

    init {
        App.appComponent.inject(this)
    }

    private fun setTextSelectable(binding: ItemPostBinding, selectable: Boolean) {
        binding.authorName.setTextIsSelectable(selectable)

        binding.tvFloor.movementMethod = LinkMovementMethod.getInstance()
        binding.tvFloor.isLongClickable = false

        binding.tvReply.setTextIsSelectable(selectable)
        binding.tvReply.movementMethod = PostMovementMethod.getInstance()
    }

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean {
        return super.isForViewType(items, position)
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPostBinding>(mLayoutInflater,
                R.layout.item_post, parent, false)
        binding.postViewModel = PostViewModel(mRxBus, mUser)

        //Bind textview lifecycle to fragment
        AndroidLifeCycle.bindFragment(binding.tvReply, fragment)

        //If setTextIsSelectable, then should reset movement
        val selectable = mGeneralPreferencesManager.isPostSelectable
        setTextSelectable(binding, selectable)

        return ItemViewBindingHolder(binding)
    }

    override fun onBindViewHolderData(post: Post, position: Int, holder: ItemViewBindingHolder, payloads: List<Any>) {
        val binding = holder.itemPostBinding

        val selectable = mGeneralPreferencesManager.isPostSelectable
        if (selectable != binding.tvReply.isTextSelectable) {
            setTextSelectable(binding, selectable)
        }

        binding.postViewModel.thread.set(threadInfo)
        binding.postViewModel.post.set(post)

        if ("1" == post.count) {
            binding.postViewModel.vote.set(voteInfo)
        } else {
            binding.postViewModel.vote.set(null)
        }

        binding.executePendingBindings()
    }

    // Bug workaround for losing text selection ability, see:
    // https://code.google.com/p/android/issues/detail?id=208169
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (mGeneralPreferencesManager.isPostSelectable) {
            val binding = (holder as ItemViewBindingHolder).itemPostBinding
            binding.authorName.isEnabled = false
            binding.tvFloor.isEnabled = false
            binding.tvReply.isEnabled = false
            binding.authorName.isEnabled = true
            binding.tvFloor.isEnabled = true
            binding.tvReply.isEnabled = true
        }
    }

    fun setThreadInfo(threadInfo: Thread) {
        this.threadInfo = threadInfo
    }

    fun setVoteInfo(voteInfo: Vote?) {
        this.voteInfo = voteInfo
    }

    class ItemViewBindingHolder(val itemPostBinding: ItemPostBinding) : RecyclerView.ViewHolder(itemPostBinding.root)
}
