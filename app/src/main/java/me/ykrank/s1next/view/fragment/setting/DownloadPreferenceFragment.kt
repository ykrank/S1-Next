package me.ykrank.s1next.view.fragment.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import com.bumptech.glide.Glide
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.disposables.Disposable
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import javax.inject.Inject

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
class DownloadPreferenceFragment : BasePreferenceFragment(), Preference.OnPreferenceClickListener {

    @Inject
    internal lateinit var mDownloadPreferencesManager: DownloadPreferencesManager

    private var disposable: Disposable? = null

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        App.appComponent.inject(this)
        addPreferencesFromResource(R.xml.preference_download)

        findPreference(getString(R.string.pref_key_clear_image_cache)).onPreferenceClickListener = this
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val key = preference.key ?: return false
        if (key == getString(R.string.pref_key_clear_image_cache)) {
            try {
                RxJavaUtil.disposeIfNotNull(disposable)
                Glide.get(App.get()).clearMemory()
                disposable = RxJavaUtil.workWithUiThread({
                    Glide.get(App.get()).clearDiskCache()
                }, {
                    activity?.toast(R.string.clear_image_cache_success)
                }, {
                    L.report(it)
                    RxJavaUtil.workInMainThread {
                        activity?.toast(R.string.clear_image_cache_error)
                    }
                })
            } catch (e: Exception) {
                L.report(e)
                activity?.toast(R.string.clear_image_cache_error)
            }

            return true
        }
        return false
    }

    override fun onDestroy() {
        RxJavaUtil.disposeIfNotNull(disposable)
        super.onDestroy()
    }

    companion object {
        val TAG: String = DownloadPreferenceFragment::class.java.name
    }
}
