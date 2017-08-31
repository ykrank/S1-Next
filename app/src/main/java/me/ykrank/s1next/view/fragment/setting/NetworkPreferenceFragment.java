package me.ykrank.s1next.view.fragment.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.NetworkPreferencesManager;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.widget.hostcheck.BaseHostUrl;
import me.ykrank.s1next.widget.hostcheck.HttpDns;

public final class NetworkPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = NetworkPreferenceFragment.class.getName();

    @Inject
    NetworkPreferencesManager mPreferencesManager;
    @Inject
    BaseHostUrl baseHostUrl;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        App.getAppComponent().inject(this);
        addPreferencesFromResource(R.xml.preference_network);

        checkForceBaseUrlSummary();
        checkForceHostIpSummary();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_force_base_url))) {
            checkForceBaseUrlSummary();
            baseHostUrl.refreshBaseHostUrl();
        } else if (key.equals(getString(R.string.pref_key_force_host_ip))) {
            checkForceHostIpSummary();
            baseHostUrl.refreshForceHostIp();
        }
    }

    private void checkForceBaseUrlSummary() {
        String baseUrl = mPreferencesManager.getForceBaseUrl();
        String key = getString(R.string.pref_key_force_base_url);
        if (TextUtils.isEmpty(baseUrl)) {
            findPreference(key).setSummary(Api.BASE_URL);
        } else {
            if (BaseHostUrl.Companion.checkBaseHostUrl(baseUrl) == null) {
                Toast.makeText(getActivity(), R.string.error_force_base_url, Toast.LENGTH_SHORT).show();
            }
            findPreference(key).setSummary(baseUrl);
        }
    }

    private void checkForceHostIpSummary() {
        String hostIp = mPreferencesManager.getForceHostIp();
        String key = getString(R.string.pref_key_force_host_ip);
        if (TextUtils.isEmpty(hostIp)) {
            findPreference(key).setSummary(getString(R.string.pref_key_force_host_ip_default_value));
        } else {
            RxJavaUtil.workWithUiResult(() -> HttpDns.checkHostIp(hostIp), inetAddresses -> {
                if (inetAddresses.size() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.error_force_host_ip, hostIp), Toast.LENGTH_SHORT).show();
                }
            });
            findPreference(key).setSummary(hostIp);
        }
    }
}
