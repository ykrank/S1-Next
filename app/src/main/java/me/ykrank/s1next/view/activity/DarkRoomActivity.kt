package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.DarkRoomFragment

/**
 * An Activity which used to send a reply.
 */
class DarkRoomActivity : BaseActivity() {

    private lateinit var mFragment: DarkRoomFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)

        setTitle(R.string.dark_room)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(DarkRoomFragment.TAG)
        if (fragment == null) {
            mFragment = DarkRoomFragment.newInstance()
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    DarkRoomFragment.TAG).commit()
        } else {
            mFragment = fragment as DarkRoomFragment
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, DarkRoomActivity::class.java)

            BaseActivity.startActivityForResultMessage(activity, intent)
        }
    }
}
