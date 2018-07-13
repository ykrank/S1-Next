package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ActivityEvent
import com.github.ykrank.androidtools.util.MathUtil
import com.github.ykrank.androidtools.widget.net.WifiBroadcastReceiver
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.event.AppNotLoginEvent
import me.ykrank.s1next.view.fragment.AppPostListPagerFragment

/**
 * An Activity which includes [android.support.v4.view.ViewPager]
 * to represent each page of post lists.
 */
class AppPostListActivity : BaseActivity(), AppPostListPagerFragment.PagerCallback, WifiBroadcastReceiver.NeedMonitorWifi {

    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_long_title)

        disableDrawerIndicator()

        thread = intent.getParcelableExtra<Thread>(ARG_THREAD)
        title = thread.title

        if (savedInstanceState == null) {
            val fragment: Fragment
            val pageNum = intent.getIntExtra(ARG_PAGE_NUM, 1)
            val postId = intent.getStringExtra(ARG_QUOTE_POST_ID)

            fragment = AppPostListPagerFragment.newInstance(thread.id, pageNum, postId)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment,
                    AppPostListPagerFragment.TAG).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        mRxBus.get()
                .ofType(AppNotLoginEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, ActivityEvent.PAUSE))
                .subscribe {
                    if (!LoginPromptDialogFragment.isShowing(supportFragmentManager)) {
                        LoginPromptDialogFragment.showAppLoginPromptDialogIfNeeded(supportFragmentManager, mUser)
                    }
                }
    }

    override fun getTotalPages(): Int {
        return MathUtil.divide(thread.reliesCount + 1, Api.POSTS_PER_PAGE)
    }

    override var threadInfo: AppThread? = null

    companion object {
        val RESULT_BLACKLIST = 11

        private const val ARG_THREAD = "thread"
        private const val ARG_PAGE_NUM = "page_num"
        private const val ARG_QUOTE_POST_ID = "quote_post_id"

        fun start(context: Context, thread: Thread, pageNum: Int, postId: String) {
            val intent = Intent(context, AppPostListActivity::class.java)
            intent.putExtra(ARG_THREAD, thread)
            if (!TextUtils.isEmpty(postId)) {
                intent.putExtra(ARG_QUOTE_POST_ID, postId)
            }
            intent.putExtra(ARG_PAGE_NUM, pageNum)

            if (context is Activity)
                context.startActivityForResult(intent, RESULT_BLACKLIST)
            else
                context.startActivity(intent)
        }
    }
}
