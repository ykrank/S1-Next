package me.ykrank.s1next.util;

import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentManager
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.widget.saf.SAFFragment

/**
 * Created by ykrank on 2017/6/6.
 */

object AppFileUtil {

    /**
     * create a random file name
     */
    fun createRandomFileName(suffix: String): String {
        val name = BuildConfig.APPLICATION_ID.replace(".", "_") + "_" + System.currentTimeMillis();
        return name + suffix;
    }

    fun getDownloadPath(
        fragmentManager: FragmentManager,
        callback: ((DocumentFile) -> Unit)?,
        focusResetPath: Boolean = false
    ) {
        var fragment = fragmentManager.findFragmentByTag(SAFFragment.TAG)
        if (fragment == null || fragment !is SAFFragment) {
            fragment = SAFFragment()
            fragmentManager.beginTransaction().add(fragment, SAFFragment.TAG)
                .commitNowAllowingStateLoss()
        }
        fragment.getDownloadPath(callback, focusResetPath);
    }
}
