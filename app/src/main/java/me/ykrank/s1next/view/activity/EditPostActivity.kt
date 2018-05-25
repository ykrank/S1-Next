package me.ykrank.s1next.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.view.fragment.EditPostFragment

/**
 * An Activity to edit post.
 */
class EditPostActivity : BaseActivity() {

    private lateinit var mFragment: EditPostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_with_ime_panel)

        setupNavCrossIcon()

        val intent = intent
        val mThread = intent.getParcelableExtra<Thread>(ARG_THREAD)
        val mPost = intent.getParcelableExtra<Post>(ARG_POST)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(EditPostFragment.TAG)
        if (fragment == null) {
            mFragment = EditPostFragment.newInstance(mThread, mPost)
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    EditPostFragment.TAG).commit()
        } else {
            mFragment = fragment as EditPostFragment
        }
    }

    /**
     * Show [android.app.AlertDialog] when reply content is not empty.
     */
    override fun onBackPressed() {
        if (mFragment.isToolsKeyboardShowing) {
            mFragment.hideToolsKeyboard()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        private val ARG_THREAD = "thread"
        private val ARG_POST = "post"

        fun startActivityForResultMessage(fragment: Fragment, requestCode: Int, thread: Thread, post: Post) {
            val intent = Intent(fragment.context, EditPostActivity::class.java)
            intent.putExtra(ARG_THREAD, thread)
            intent.putExtra(ARG_POST, post)
            fragment.startActivityForResult(intent, requestCode)
        }
    }
}
