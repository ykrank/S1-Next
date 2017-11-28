package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import com.github.ykrank.androidtools.ui.LibBaseFragment
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.github.ykrank.androidtools.widget.track.event.page.FragmentEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.FragmentStartEvent
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.UserValidator
import javax.inject.Inject

abstract class BaseFragment : LibBaseFragment() {

    @Inject
    internal lateinit var mUserValidator: UserValidator
    @Inject
    internal lateinit var trackAgent: DataTrackAgent
    @Inject
    internal lateinit var mUser: User

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        trackAgent.post(FragmentStartEvent(this))
    }

    override fun onPause() {
        trackAgent.post(FragmentEndEvent(this))
        super.onPause()
    }
}
