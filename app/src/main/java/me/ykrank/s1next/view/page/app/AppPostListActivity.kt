package me.ykrank.s1next.view.page.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ActivityEvent
import com.github.ykrank.androidtools.widget.net.WifiBroadcastReceiver
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.event.AppNotLoginEvent

/**
 * An Activity which includes [android.support.v4.view.ViewPager]
 * to represent each page of post lists.
 */
class AppPostListActivity : BaseActivity(), AppPostListPagerFragment.PagerCallback, WifiBroadcastReceiver.NeedMonitorWifi {

    private lateinit var threadId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_long_title)

        disableDrawerIndicator()

        threadId = intent.getStringExtra(ARG_THREAD_ID)

        if (savedInstanceState == null) {
            val fragment: androidx.fragment.app.Fragment
            val pageNum = intent.getIntExtra(ARG_PAGE_NUM, 1)
            val postId = intent.getStringExtra(ARG_QUOTE_POST_ID)

            if (postId != null) {
                fragment = AppPostListPagerFragment.newInstance(threadId, pageNum, postId)
                supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment,
                        AppPostListPagerFragment.TAG).commit()
            } else {
                fragment = AppPostListFragment.newInstance(threadId, pageNum, postId)
                supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment,
                        AppPostListFragment.TAG).commit()
            }
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

    override fun setTotalPages(page: Int?) {
        title = "${threadInfo?.subject} $page"
    }

    override var threadInfo: AppThread? = null

    companion object {
        private const val RESULT_BLACKLIST = 11

        private const val ARG_THREAD_ID = "thread_id"
        private const val ARG_PAGE_NUM = "page_num"
        private const val ARG_QUOTE_POST_ID = "quote_post_id"

        fun start(context: Context, threadId: String, pageNum: Int, postId: String?) {
            val intent = Intent(context, AppPostListActivity::class.java)
            intent.putExtra(ARG_THREAD_ID, threadId)
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
