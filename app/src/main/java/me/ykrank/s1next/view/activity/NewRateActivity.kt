package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.NewRateFragment

/**
 * An Activity which used to send a reply.
 */
class NewRateActivity : BaseActivity() {

    private lateinit var mFragment: NewRateFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)

        val intent = intent
        val threadId = intent.getStringExtra(ARG_THREAD_ID)
        val postID = intent.getStringExtra(ARG_POST_ID)
        setTitle(R.string.title_new_rate)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(NewRateFragment.TAG)
        if (fragment == null) {
            mFragment = NewRateFragment.newInstance(threadId, postID)
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    NewRateFragment.TAG).commit()
        } else {
            mFragment = fragment as NewRateFragment
        }
    }

    companion object {

        private val ARG_THREAD_ID = "thread_id"
        private val ARG_POST_ID = "post_id"

        fun start(activity: Activity, threadId: String, postId: String) {
            val intent = Intent(activity, NewRateActivity::class.java)
            intent.putExtra(ARG_THREAD_ID, threadId)
            intent.putExtra(ARG_POST_ID, postId)

            BaseActivity.startActivityForResultMessage(activity, intent)
        }
    }
}
