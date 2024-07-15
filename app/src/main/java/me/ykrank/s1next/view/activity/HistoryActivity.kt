package me.ykrank.s1next.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.R
import me.ykrank.s1next.view.fragment.HistoryListFragment
import me.ykrank.s1next.view.fragment.HistoryListFragment.Companion.newInstance
import me.ykrank.s1next.widget.track.event.ViewHistoryTrackEvent

/**
 * Activity show post view history list
 */
class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)
        trackAgent.post(ViewHistoryTrackEvent())
        L.leaveMsg("HistoryActivity")
        if (savedInstanceState == null) {
            val fragment: Fragment = newInstance()
            supportFragmentManager.beginTransaction().add(
                R.id.frame_layout, fragment,
                HistoryListFragment.TAG
            ).commit()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HistoryActivity::class.java)
            context.startActivity(intent)
        }
    }
}
