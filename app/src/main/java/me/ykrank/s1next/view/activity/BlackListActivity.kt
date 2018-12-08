package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.setting.BlackListSettingFragment

/**
 * An Activity which used to send a reply.
 */
class BlackListActivity : BaseActivity() {

    private lateinit var mFragment: BlackListSettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)

        setTitle(R.string.blacklist)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(BlackListSettingFragment.TAG)
        if (fragment == null) {
            mFragment = BlackListSettingFragment.newInstance()
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    BlackListSettingFragment.TAG).commit()
        } else {
            mFragment = fragment as BlackListSettingFragment
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, BlackListActivity::class.java)

            BaseActivity.startActivityForResultMessage(activity, intent)
        }
    }
}
