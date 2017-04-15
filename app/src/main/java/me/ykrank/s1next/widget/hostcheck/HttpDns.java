package me.ykrank.s1next.widget.hostcheck;


import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.util.L;
import okhttp3.Dns;

public class HttpDns implements Dns {
    private static final Dns SYSTEM = Dns.SYSTEM;

    private final GeneralPreferencesManager preferencesManager;

    private List<InetAddress> inetAddressList;
    private String forceHostIp;

    public HttpDns(GeneralPreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        checkInetAddress();
        if (inetAddressList != null) {
            if (Api.BASE_HOST.equals(hostname)) {
                return inetAddressList;
            }
        }
        return SYSTEM.lookup(hostname);
    }

    private void checkInetAddress() {
        try {
            if (inetAddressList == null || inetAddressList.isEmpty() ||
                    !TextUtils.equals(forceHostIp, preferencesManager.getForceHostIp())) {

                inetAddressList = Arrays.asList(InetAddress.getAllByName(preferencesManager.getForceHostIp()));
                forceHostIp = preferencesManager.getForceHostIp();
            }
        } catch (UnknownHostException e) {
            L.toast(R.string.error_host_ip);
        }
    }
}
