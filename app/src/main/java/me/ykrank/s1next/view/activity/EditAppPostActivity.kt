package me.ykrank.s1next.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.view.fragment.EditAppPostFragment

/**
 * An Activity to new a thread.
 */
class EditAppPostActivity : BaseActivity() {

    private lateinit var mFragment: EditAppPostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)

        setupNavCrossIcon()

        val intent = intent
        val mThread = intent.getParcelableExtra<AppThread>(ARG_THREAD)
        val mPost = intent.getParcelableExtra<AppPost>(ARG_POST)
        setTitle(R.string.title_new_thread)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(EditAppPostFragment.TAG)
        if (fragment == null) {
            mFragment = EditAppPostFragment.newInstance(mThread, mPost)
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    EditAppPostFragment.TAG).commit()
        } else {
            mFragment = fragment as EditAppPostFragment
        }
    }

    /**
     * Show [android.app.AlertDialog] when reply content is not empty.
     */
    override fun onBackPressed() {
        if (mFragment.isEmoticonKeyboardShowing) {
            mFragment.hideEmoticonKeyboard()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        private val ARG_THREAD = "thread"
        private val ARG_POST = "post"

        fun startActivityForResultMessage(fragment: Fragment, requestCode: Int, thread: AppThread, post: AppPost) {
            val intent = Intent(fragment.context, EditAppPostActivity::class.java)
            intent.putExtra(ARG_THREAD, thread)
            intent.putExtra(ARG_POST, post)
            fragment.startActivityForResult(intent, requestCode)
        }
    }
}
