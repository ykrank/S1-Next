package me.ykrank.s1next.util;

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentManager
import com.github.ykrank.androidtools.util.FileUtil
import me.ykrank.s1next.widget.saf.SAFFragment

/**
 * Created by ykrank on 2017/6/6.
 */

object AppFileUtil {

    /**
     * create a random file name
     */
    fun createRandomFileName(context: Context, suffix: String): String {
        return FileUtil.createRandomFileName(context, suffix)
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
