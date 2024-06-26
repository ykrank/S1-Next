package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import me.ykrank.s1next.R
import me.ykrank.s1next.view.page.post.postedit.NewPmFragment

/**
 * An Activity which used to send a reply.
 */
class NewPmActivity : BaseActivity() {

    private lateinit var newPmFragment: NewPmFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_with_ime_panel)

        setupNavCrossIcon()

        val intent = intent
        val toUid = intent.getStringExtra(ARG_TO_UID)
        val toUsername = intent.getStringExtra(ARG_TO_USERNAME)
        if (TextUtils.isEmpty(toUid)) {
            showSnackbar(R.string.message_api_error)
            return
        }
        title = getString(R.string.title_new_pm) + "-" + toUsername

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(NewPmFragment.TAG)
        if (fragment == null) {
            newPmFragment = NewPmFragment.newInstance(toUid!!)
            fragmentManager.beginTransaction().add(R.id.frame_layout, newPmFragment,
                    NewPmFragment.TAG).commit()
        } else {
            newPmFragment = fragment as NewPmFragment
        }
    }

    /**
     * Show [android.support.v7.app.AlertDialog] when reply content is not empty.
     */
    override fun onBackPressed() {
        if (newPmFragment.isToolsKeyboardShowing) {
            newPmFragment.hideToolsKeyboard()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        private const val ARG_TO_UID = "arg_to_uid"
        private const val ARG_TO_USERNAME = "to_user_name"

        fun startNewPmActivityForResultMessage(activity: Activity, toUid: String?, toUsername: String?) {
            if (toUid.isNullOrEmpty() || toUsername.isNullOrEmpty()) {
                return
            }
            val intent = Intent(activity, NewPmActivity::class.java)
            intent.putExtra(ARG_TO_UID, toUid)
            intent.putExtra(ARG_TO_USERNAME, toUsername)

            BaseActivity.startActivityForResultMessage(activity, intent)
        }
    }
}
