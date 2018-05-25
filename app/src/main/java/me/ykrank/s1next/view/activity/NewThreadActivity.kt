package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.NewThreadFragment

/**
 * An Activity to new a thread.
 */
class NewThreadActivity : BaseActivity() {

    private lateinit var mNewThreadFragment: NewThreadFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_with_ime_panel)

        setupNavCrossIcon()

        val intent = intent
        val forumId = intent.getIntExtra(ARG_FORUM_ID, 75)
        setTitle(R.string.title_new_thread)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(NewThreadFragment.TAG)
        if (fragment == null) {
            mNewThreadFragment = NewThreadFragment.newInstance(forumId)
            fragmentManager.beginTransaction().add(R.id.frame_layout, mNewThreadFragment,
                    NewThreadFragment.TAG).commit()
        } else {
            mNewThreadFragment = fragment as NewThreadFragment
        }
    }

    /**
     * Show [android.app.AlertDialog] when reply content is not empty.
     */
    override fun onBackPressed() {
        if (mNewThreadFragment.isToolsKeyboardShowing) {
            mNewThreadFragment.hideToolsKeyboard()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        private val ARG_FORUM_ID = "forum_id"

        fun startNewThreadActivityForResultMessage(activity: Activity, forumId: Int) {
            val intent = Intent(activity, NewThreadActivity::class.java)
            intent.putExtra(ARG_FORUM_ID, forumId)

            BaseActivity.startActivityForResultMessage(activity, intent)
        }
    }
}
