package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import com.github.ykrank.androidlifecycle.AndroidLifeCycle
import com.github.ykrank.androidtools.ui.adapter.simple.BindViewHolderCallback
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.ItemPostBinding
import me.ykrank.s1next.databinding.ItemRateDetailBinding
import me.ykrank.s1next.view.activity.RateDetailsListActivity
import me.ykrank.s1next.view.activity.UserHomeActivity
import me.ykrank.s1next.viewmodel.PostViewModel
import me.ykrank.s1next.widget.glide.AvatarUrlsCache
import me.ykrank.s1next.widget.span.PostMovementMethod
import javax.inject.Inject

class PostAdapterDelegate(private val fragment: Fragment, context: Context) :
        BaseAdapterDelegate<Post, SimpleRecycleViewHolder<ItemPostBinding>>(context, Post::class.java) {

    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mUser: User
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager
    private var threadInfo: Thread? = null
    private var voteInfo: Vote? = null
    private var pageNum: Int = 1

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

        return SimpleRecycleViewHolder<ItemPostBinding>(binding)
    }

    override fun onBindViewHolderData(post: Post, position: Int, holder: SimpleRecycleViewHolder<ItemPostBinding>, payloads: List<Any>) {
        val binding = holder.binding

        val selectable = mGeneralPreferencesManager.isPostSelectable
        if (selectable != binding.tvReply.isTextSelectable) {
            setTextSelectable(binding, selectable)
        }

        binding.postViewModel?.let {
            it.thread.set(threadInfo)
            it.pageNum.set(pageNum)
            it.post.set(post)

            if ("1" == post.count) {
                it.vote.set(voteInfo)
            } else {
                it.vote.set(null)
            }
        }

        val rates = post.rates
        if (rates != null && rates.isNotEmpty()) {
            val context = binding.root.context
            if (binding.recycleViewRates.adapter == null) {
                binding.recycleViewRates.adapter = SimpleRecycleViewAdapter(context, R.layout.item_rate_detail, BindViewHolderCallback { position, binding ->
                    val bind = binding as ItemRateDetailBinding?
                    bind?.model?.apply {
                        val uid = this.uid
                        val uname = this.uname
                        bind.avatar.setOnClickListener {
                            if (uid != null && uname != null) {
                                //Clear avatar false cache
                                AvatarUrlsCache.clearUserAvatarCache(uid)
                                //个人主页
                                UserHomeActivity.start(it.context as FragmentActivity, uid, uname, it)
                            }
                        }
                    }

                })
                binding.recycleViewRates.layoutManager = LinearLayoutManager(context)
                binding.recycleViewRates.isNestedScrollingEnabled = false
            }
            val adapter = binding.recycleViewRates.adapter as SimpleRecycleViewAdapter

            if (rates.size > 10) {
                adapter.swapDataSet(rates.subList(0, 10))
            } else {
                adapter.swapDataSet(rates)
            }

            binding.tvRateViewAll.setOnClickListener {
                RateDetailsListActivity.start(context, ArrayList(rates))
            }
        }

        binding.executePendingBindings()
    }

    // Bug workaround for losing text selection ability, see:
    // https://code.google.com/p/android/issues/detail?id=208169
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (mGeneralPreferencesManager.isPostSelectable) {
            val binding = (holder as SimpleRecycleViewHolder<ItemPostBinding>).binding
            binding.authorName.isEnabled = false
            binding.tvFloor.isEnabled = false
            binding.tvReply.isEnabled = false
            binding.authorName.isEnabled = true
            binding.tvFloor.isEnabled = true
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
