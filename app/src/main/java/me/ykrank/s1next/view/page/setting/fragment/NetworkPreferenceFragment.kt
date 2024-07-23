package me.ykrank.s1next.view.page.setting.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.hostcheck.BaseDns
import com.github.ykrank.androidtools.widget.hostcheck.BaseHostUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.pref.NetworkPreferencesManager
import me.ykrank.s1next.widget.hostcheck.AppHostUrl
import javax.inject.Inject

class NetworkPreferenceFragment : BasePreferenceFragment() {

    @Inject
    lateinit var mPreferencesManager: NetworkPreferencesManager

    @Inject
    lateinit var baseHostUrl: AppHostUrl

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        appComponent.inject(this)
        addPreferencesFromResource(R.xml.preference_network)

        checkForceBaseUrlSummary()
        checkForceHostIpSummary()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == getString(R.string.pref_key_force_base_url)) {
            checkForceBaseUrlSummary()
            baseHostUrl.refreshBaseHostUrl()
        } else if (key == getString(R.string.pref_key_force_host_ip)) {
            checkForceHostIpSummary()
            baseHostUrl.refreshForceHostIp()
        }
    }

    private fun checkForceBaseUrlSummary() {
        val baseUrl = mPreferencesManager.forceBaseUrl
        val key = getString(R.string.pref_key_force_base_url)
        if (baseUrl.isNullOrEmpty()) {
            findPreference<Preference>(key)?.summary = Api.BASE_URL
        } else {
            if (BaseHostUrl.checkBaseHostUrl(baseUrl) == null) {
                Toast.makeText(activity, R.string.error_force_base_url, Toast.LENGTH_SHORT).show()
            }
            findPreference<Preference>(key)?.summary = baseUrl
        }
    }

    private fun checkForceHostIpSummary() {
        val hostIp = mPreferencesManager.forceHostIp
        val key = getString(R.string.pref_key_force_host_ip)
        if (TextUtils.isEmpty(hostIp)) {
            findPreference<Preference>(key)?.summary =
                getString(R.string.pref_key_force_host_ip_default_value)
        } else {
            lifecycleScope.launch(L.report) {
                val inetAddresses = withContext(Dispatchers.IO) {
                    BaseDns.checkHostIp(hostIp)
                }
                if (inetAddresses.isEmpty()) {
                    Toast.makeText(
                        activity,
                        getString(R.string.error_force_host_ip, hostIp),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            findPreference<Preference>(key)?.summary = hostIp
        }
    }

    companion object {
        val TAG: String = NetworkPreferenceFragment::class.java.simpleName
    }
}
