package me.ykrank.s1next.widget.hostcheck;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.util.LooperUtil;
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
            if (inetAddressList != null && inetAddressList.size() > 0 && baseHostUrl.getBaseHttpUrl() != null
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
            inetAddressList = checkHostIp(baseHostUrl.getHostIp());
            forceHostIp = baseHostUrl.getHostIp();
        }
    }

    /**
     * Check whether this ip list is valid. ip should like
     *
     * @param hostIpList ip list, sep by ','
     * @return valid InetAddress
     */
    @WorkerThread
    @NonNull
    public static List<InetAddress> checkHostIp(@Nullable String hostIpList) {
        LooperUtil.enforceOnWorkThread();
        List<InetAddress> inetAddressList = new ArrayList<>();
        if (!TextUtils.isEmpty(hostIpList)) {
            String[] hostIps = hostIpList.split(",");
            for (String hostIp : hostIps) {
                try {
                    inetAddressList.add(InetAddress.getByName(hostIp.trim()));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        return inetAddressList;
    }
}
