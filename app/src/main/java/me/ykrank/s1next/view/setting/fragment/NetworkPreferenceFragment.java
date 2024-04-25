package me.ykrank.s1next.view.setting.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.widget.hostcheck.BaseDns;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.NetworkPreferencesManager;
import me.ykrank.s1next.widget.hostcheck.AppHostUrl;

public final class NetworkPreferenceFragment extends BasePreferenceFragment {
    public static final String TAG = NetworkPreferenceFragment.class.getName();

    @Inject
    NetworkPreferencesManager mPreferencesManager;
    @Inject
    AppHostUrl baseHostUrl;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        App.Companion.getAppComponent().inject(this);
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
            if (AppHostUrl.Companion.checkBaseHostUrl(baseUrl) == null) {
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
            RxJavaUtil.workWithUiResult(() -> BaseDns.checkHostIp(hostIp), inetAddresses -> {
                if (inetAddresses.size() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.error_force_host_ip, hostIp), Toast.LENGTH_SHORT).show();
                }
            });
            findPreference(key).setSummary(hostIp);
        }
    }
}
