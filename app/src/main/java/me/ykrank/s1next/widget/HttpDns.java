package me.ykrank.s1next.widget;


import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import me.ykrank.s1next.data.api.Api;
import okhttp3.Dns;

public class HttpDns implements Dns {
    private static final Dns SYSTEM = Dns.SYSTEM;

    private List<InetAddress> inetAddressList;

    public HttpDns() {
        if (!TextUtils.isEmpty(Api.FORCE_HOST_IP)) {
            try {
                inetAddressList = Arrays.asList(InetAddress.getAllByName(Api.FORCE_HOST_IP));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (inetAddressList != null && Api.BASE_HOST.equals(hostname)) {
            return inetAddressList;
        }
        return SYSTEM.lookup(hostname);
    }
}
