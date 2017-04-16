package me.ykrank.s1next.widget.hostcheck;


import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

public class HttpDns implements Dns {
    private static final Dns SYSTEM = Dns.SYSTEM;

    private final BaseHostUrl baseHostUrl;

    private List<InetAddress> inetAddressList;
    private String forceHostIp;

    public HttpDns(BaseHostUrl baseHostUrl) {
        this.baseHostUrl = baseHostUrl;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        try {
            checkInetAddress();
            if (inetAddressList != null && baseHostUrl.getBaseHttpUrl() != null
                    && TextUtils.equals(baseHostUrl.getBaseHttpUrl().host(), hostname)) {
                return inetAddressList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SYSTEM.lookup(hostname);
    }

    public void checkInetAddress() throws UnknownHostException {
        if (!TextUtils.equals(forceHostIp, baseHostUrl.getHostIp())) {
            inetAddressList = Arrays.asList(InetAddress.getAllByName(baseHostUrl.getHostIp()));
            forceHostIp = baseHostUrl.getHostIp();
        }
    }

    /**
     * Check whether this ip list is valid. ip should like
     *
     * @param hostIp ip list, sep by ','
     * @return valid
     */
    public static boolean checkHostIp(String hostIp) {
        try {
            return InetAddress.getAllByName(hostIp).length > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
