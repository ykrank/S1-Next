package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences

import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api

/**
 * A helper class retrieving the network preferences from [SharedPreferences].
 */
class NetworkPreferencesImpl(context: Context, sharedPreferences: SharedPreferences)
    : BasePreferences(context, sharedPreferences), NetworkPreferences {

    override val isForceBaseUrlEnable: Boolean
        get() = getPrefBoolean(R.string.pref_key_force_base_url_enabled,
                R.bool.pref_key_force_base_url_enabled_default_value)

    override var forceBaseUrl: String
        get() = getPrefString(R.string.pref_key_force_base_url, Api.BASE_URL)
        set(baseUrl) = putPrefString(R.string.pref_key_force_base_url, baseUrl)

    override var isAutoCheckBaseUrl: Boolean
        get() = getPrefBoolean(R.string.pref_key_auto_check_base_url,
                R.bool.pref_key_auto_check_base_url_default_value)
        set(autoCheck) = putPrefBoolean(R.string.pref_key_auto_check_base_url, autoCheck)

    override val isForceHostIpEnable: Boolean
        get() = getPrefBoolean(R.string.pref_key_force_host_ip_enabled,
                R.bool.pref_key_force_host_ip_enabled_default_value)

    override var forceHostIp: String
        get() = getPrefString(R.string.pref_key_force_host_ip,
                mContext.resources.getString(R.string.pref_key_force_host_ip_default_value))
        set(hostIp) = putPrefString(R.string.pref_key_force_host_ip, hostIp)
}

interface NetworkPreferences {
    val isForceBaseUrlEnable: Boolean
    var forceBaseUrl: String
    var isAutoCheckBaseUrl: Boolean
    val isForceHostIpEnable: Boolean
    var forceHostIp: String
}

class NetworkPreferencesManager(private val mPreferencesProvider: NetworkPreferences) : NetworkPreferences by mPreferencesProvider