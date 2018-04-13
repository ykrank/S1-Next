package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.github.ykrank.androidtools.data.BasePreferences
import com.github.ykrank.androidtools.data.PreferenceDelegates

import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api

/**
 * A helper class retrieving the network preferences from [SharedPreferences].
 */
class NetworkPreferencesImpl(context: Context, sharedPreferences: SharedPreferences)
    : BasePreferences(context, sharedPreferences), NetworkPreferences {

    override val isForceBaseUrlEnable: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_force_base_url_enabled, R.bool.pref_key_force_base_url_enabled_default_value)

    override var forceBaseUrl: String? by PreferenceDelegates.string(
            mContext.getString(R.string.pref_key_force_base_url), Api.BASE_URL)

    override var isAutoCheckBaseUrl: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_auto_check_base_url, R.bool.pref_key_auto_check_base_url_default_value)

    override val isForceHostIpEnable: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_force_host_ip_enabled, R.bool.pref_key_force_host_ip_enabled_default_value)

    override var forceHostIp: String? by PreferenceDelegates.string(
            R.string.pref_key_force_host_ip, R.string.pref_key_force_host_ip_default_value)
}

interface NetworkPreferences {
    val isForceBaseUrlEnable: Boolean
    var forceBaseUrl: String?
    var isAutoCheckBaseUrl: Boolean
    val isForceHostIpEnable: Boolean
    var forceHostIp: String?
}

class NetworkPreferencesManager(private val mPreferencesProvider: NetworkPreferences) : NetworkPreferences by mPreferencesProvider