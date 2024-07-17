package com.github.ykrank.androidtools.ui

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentManager
import com.github.ykrank.androidtools.widget.TagFragmentStatePagerAdapter

/**
 * A base [TagFragmentStatePagerAdapter] wraps some implement.
 */
abstract class LibBaseFragmentStatePagerAdapter<T : LibBaseFragment>(fm: FragmentManager) :
    TagFragmentStatePagerAdapter<T>(fm) {

    var currentFragment: T? = null
        private set


    @CallSuper
    override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: T) {
        if (currentFragment !== fragment) {
            currentFragment = fragment
        }

        super.setPrimaryItem(container, position, fragment)
    }

    @CallSuper
    override fun destroyItem(container: ViewGroup, position: Int, fragment: T?) {
        super.destroyItem(container, position, fragment)
    }
}