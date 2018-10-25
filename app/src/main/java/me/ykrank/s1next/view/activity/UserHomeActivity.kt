package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ActivityEvent
import com.github.ykrank.androidtools.guava.Optional
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.util.*
import com.github.ykrank.androidtools.widget.AppBarOffsetChangedListener
import com.github.ykrank.androidtools.widget.glide.model.ImageInfo
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.Profile
import me.ykrank.s1next.data.db.BlackListDbWrapper
import me.ykrank.s1next.databinding.ActivityHomeBinding
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.event.BlackListChangeEvent
import me.ykrank.s1next.view.internal.BlacklistMenuAction
import me.ykrank.s1next.widget.track.event.ViewHomeTrackEvent
import javax.inject.Inject

/**
 * Created by ykrank on 2017/1/8.
 */

class UserHomeActivity : BaseActivity() {

    @Inject
    internal lateinit var s1Service: S1Service

    private lateinit var binding: ActivityHomeBinding
    private var uid: String? = null
    private var name: String? = null
    private var isInBlacklist: Boolean = false
    private var blacklistMenu: MenuItem? = null
    private lateinit var adapter: SimpleRecycleViewAdapter

    override val isTranslucent: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)

        uid = intent.getStringExtra(ARG_UID)
        name = intent.getStringExtra(ARG_USERNAME)
        val thumbImageInfo = intent.getParcelableExtra<ImageInfo>(ARG_IMAGE_INFO)
        trackAgent.post(ViewHomeTrackEvent(uid, name))
        L.leaveMsg("UserHomeActivity##uid:$uid,name:$name")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.downloadPreferencesManager = mDownloadPreferencesManager
        binding.big = true
        binding.preLoad = true
        binding.thumb = thumbImageInfo?.url
        val profile = Profile()
        profile.homeUid = uid
        profile.homeUsername = name
        binding.data = profile

        binding.appBar.addOnOffsetChangedListener(object : AppBarOffsetChangedListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, oldVerticalOffset: Int, verticalOffset: Int) {
                val maxScroll = appBarLayout.totalScrollRange
                val oldPercentage = Math.abs(oldVerticalOffset).toFloat() / maxScroll.toFloat()
                val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()
                if (oldPercentage < PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR && percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                    //Move up
                    AnimUtils.startAlphaAnimation(binding.toolbarTitle, TITLE_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                } else if (oldPercentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR && percentage < PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                    //Move down
                    AnimUtils.startAlphaAnimation(binding.toolbarTitle, TITLE_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                }
            }
        })

        binding.avatar.setOnClickListener { v ->
            val bigAvatarUrl = Api.getAvatarBigUrl(uid)
            GalleryActivity.start(v.context, bigAvatarUrl)
        }

        binding.ivNewPm.setOnClickListener { v ->
            binding.data?.let {
                NewPmActivity.startNewPmActivityForResultMessage(this,
                        it.homeUid, it.homeUsername)
            }
        }

        binding.tvFriends.setOnClickListener { v -> FriendListActivity.start(this, uid, name) }

        binding.tvThreads.setOnClickListener { v -> UserThreadActivity.start(this, uid, name) }

        binding.tvReplies.setOnClickListener { v -> UserReplyActivity.start(this, uid, name) }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.isNestedScrollingEnabled = false
        adapter = SimpleRecycleViewAdapter(this, R.layout.item_home_stat)
        binding.recyclerView.adapter = adapter

        setupImage()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        blacklistMenu = menu.findItem(R.id.menu_blacklist)
        refreshBlacklistMenu()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_blacklist -> {
                if (isInBlacklist) {
                    BlacklistMenuAction.removeBlacklist(mRxBus, uid?.toInt() ?: 0, name)
                } else {
                    BlacklistMenuAction.addBlacklist(this, uid?.toInt() ?: 0, name)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        mRxBus.get()
                .ofType(BlackListChangeEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, ActivityEvent.PAUSE))
                .subscribe { blackListEvent ->
                    val dbWrapper = BlackListDbWrapper.getInstance()
                    if (blackListEvent.isAdd) {
                        Single.just(true)
                                .doOnSuccess { b ->
                                    dbWrapper.saveDefaultBlackList(
                                            blackListEvent.authorPostId, blackListEvent.authorPostName,
                                            blackListEvent.remark)
                                }
                                .compose(RxJavaUtil.iOSingleTransformer())
                                .subscribe({ this.afterBlackListChange(it) }, { L.report(it) })
                    } else {
                        Single.just(false)
                                .doOnSuccess { b ->
                                    dbWrapper.delDefaultBlackList(blackListEvent.authorPostId,
                                            blackListEvent.authorPostName)
                                }
                                .compose(RxJavaUtil.iOSingleTransformer())
                                .subscribe({ this.afterBlackListChange(it) }, { L.report(it) })
                    }
                }
    }

    private fun afterBlackListChange(isAdd: Boolean) {
        showShortToast(if (isAdd) R.string.blacklist_add_success else R.string.blacklist_remove_success)
        refreshBlacklistMenu()
    }

    private fun setupImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.sharedElementEnterTransition.addListener(object : TransitionUtils.TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    super.onTransitionEnd(transition)
                    binding.big = true
                    binding.preLoad = false
                }
            })
        } else {
            binding.big = true
            binding.preLoad = false
        }
    }

    private fun loadData() {
        binding.data?.let { profile ->
            s1Service.getProfileWeb("https://bbs.saraba1st.com/2b/space-uid-${profile.homeUid}.html", profile.homeUid)
                    .map { Profile.fromHtml(it) }
                    .compose(RxJavaUtil.iOSingleTransformer())
                    .to(AndroidRxDispose.withSingle(this, ActivityEvent.DESTROY))
                    .subscribe({
                        binding.data = it
                        adapter.swapDataSet(it.stats)
                    }, L::e)
        }
    }

    @MainThread
    private fun refreshBlacklistMenu() {
        if (blacklistMenu == null) {
            return
        }
        val wrapper = BlackListDbWrapper.getInstance()
        Single.just(Optional.fromNullable(wrapper.getMergedBlackList(uid?.toInt() ?: 0, name)))
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, ActivityEvent.DESTROY))
                .subscribe({ blackListOptional ->
                    if (blackListOptional.isPresent) {
                        isInBlacklist = true
                        blacklistMenu?.setTitle(R.string.menu_blacklist_remove)
                    } else {
                        isInBlacklist = false
                        blacklistMenu?.setTitle(R.string.menu_blacklist_add)
                    }
                }, { L.report(it) })
    }

    companion object {

        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.71f
        private const val TITLE_ANIMATIONS_DURATION = 300

        private const val ARG_UID = "uid"
        private const val ARG_USERNAME = "username"
        private const val ARG_IMAGE_INFO = "image_info"

        fun start(activity: FragmentActivity, uid: String, userName: String?) {
            if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(activity.supportFragmentManager, App.appComponent.user)) {
                return
            }

            val intent = Intent(activity, UserHomeActivity::class.java)
            intent.putExtra(ARG_UID, uid)
            intent.putExtra(ARG_USERNAME, userName)
            activity.startActivity(intent)
        }

        fun start(activity: FragmentActivity, uid: String, userName: String?, avatarView: View) {
            //@see http://stackoverflow.com/questions/31381385/nullpointerexception-drawable-setbounds-probably-due-to-fragment-transitions#answer-31383033
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                start(activity, uid, userName)
                return
            }
            if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(activity.supportFragmentManager, App.appComponent.user)) {
                return
            }

            val baseContext = ContextUtils.getBaseContext(activity)
            if (baseContext !is Activity) {
                L.leaveMsg("uid:$uid")
                L.leaveMsg("userName:$userName")
                L.report(IllegalStateException("UserHomeActivity start error: context not instance of activity"))
                return
            }
            val imageInfo = avatarView.getTag(R.id.tag_drawable_info) as ImageInfo?
            val intent = Intent(baseContext, UserHomeActivity::class.java)
            intent.putExtra(ARG_UID, uid)
            intent.putExtra(ARG_USERNAME, userName)
            if (imageInfo != null) {
                intent.putExtra(ARG_IMAGE_INFO, imageInfo)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    baseContext, avatarView, baseContext.getString(R.string.transition_avatar))
            ActivityCompat.startActivity(baseContext, intent, options.toBundle())
        }
    }
}
