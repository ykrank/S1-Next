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

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (TextUtils.isEmpty(Api.FORSE_HOST_IP)) {
            if (Api.BASE_HOST.equals(hostname)) {
                return Arrays.asList(InetAddress.getAllByName(Api.FORSE_HOST_IP));
            }
        }
        return SYSTEM.lookup(hostname);
    }
}
