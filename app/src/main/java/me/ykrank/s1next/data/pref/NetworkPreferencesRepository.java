package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;

/**
 * A helper class retrieving the network preferences from {@link SharedPreferences}.
 */
public final class NetworkPreferencesRepository extends BasePreferencesRepository {

    public NetworkPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }

    public boolean isForceBaseUrlEnable() {
        return getPrefBoolean(R.string.pref_key_force_base_url_enabled,
                R.bool.pref_key_force_base_url_enabled_default_value);
    }

    public String getForceBaseUrl() {
        return getPrefString(R.string.pref_key_force_base_url, Api.BASE_URL);
    }

    public void setForceBaseUrl(String baseUrl) {
        putPrefString(R.string.pref_key_force_base_url, baseUrl);
    }

    public boolean isAutoCheckBaseUrl() {
        return getPrefBoolean(R.string.pref_key_auto_check_base_url,
                R.bool.pref_key_auto_check_base_url_default_value);
    }

    public void setAutoCheckBaseUrl(boolean autoCheck) {
        putPrefBoolean(R.string.pref_key_auto_check_base_url, autoCheck);
    }

    public boolean isForceHostIpEnable() {
        return getPrefBoolean(R.string.pref_key_force_host_ip_enabled,
                R.bool.pref_key_force_host_ip_enabled_default_value);
    }

    public String getForceHostIp() {
        return getPrefString(R.string.pref_key_force_host_ip,
                mContext.getResources().getString(R.string.pref_key_force_host_ip_default_value));
    }

    public void setForceHostIp(String hostIp) {
        putPrefString(R.string.pref_key_force_host_ip, hostIp);
    }
}
