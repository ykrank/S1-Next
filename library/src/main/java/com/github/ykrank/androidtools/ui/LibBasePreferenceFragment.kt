package com.github.ykrank.androidtools.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.preference.PreferenceFragmentCompat
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentStartEvent

/**
 * A helper class for registering/unregistering
 * [android.content.SharedPreferences.OnSharedPreferenceChangeListener].
 */
abstract class LibBasePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.leaveMsg("${this.javaClass.simpleName} onCreate")
    }

    @CallSuper
    override fun onStart() {
        super.onStart()

        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    @CallSuper
    override fun onStop() {
        super.onStop()

        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        UiGlobalData.provider?.trackAgent?.post(LocalFragmentStartEvent(this))
    }

    @CallSuper
    override fun onPause() {
        UiGlobalData.provider?.trackAgent?.post(LocalFragmentEndEvent(this))
        super.onPause()
    }

    @CallSuper
    override fun onDestroy() {
        L.leaveMsg("${this.javaClass.simpleName} onDestroy")
        super.onDestroy()
    }

    protected fun leavePageMsg(msg: String) {
        L.leaveMsg(msg)
    }
}
