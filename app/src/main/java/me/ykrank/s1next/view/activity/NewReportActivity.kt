package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.NewReportFragment

/**
 * An Activity which used to send a reply.
 */
class NewReportActivity : BaseActivity() {

    private lateinit var mFragment: NewReportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)

        val intent = intent
        val threadId = intent.getStringExtra(ARG_THREAD_ID)
        val postID = intent.getStringExtra(ARG_POST_ID)
        val pageNum = intent.getIntExtra(ARG_PAGE_NUM, 1)
        setTitle(R.string.title_new_report)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(NewReportFragment.TAG)
        if (fragment == null) {
            mFragment = NewReportFragment.newInstance(threadId, postID, pageNum)
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    NewReportFragment.TAG).commit()
        } else {
            mFragment = fragment as NewReportFragment
        }
    }

    companion object {

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_POST_ID = "post_id"
        private val ARG_PAGE_NUM = "page_num"

        fun start(activity: Activity, threadId: String, postId: String, pageNum:Int) {
            val intent = Intent(activity, NewReportActivity::class.java)
            intent.putExtra(ARG_THREAD_ID, threadId)
            intent.putExtra(ARG_POST_ID, postId)
            intent.putExtra(ARG_PAGE_NUM, pageNum)

            BaseActivity.startActivityForResultMessage(activity, intent)
        }
    }
}
