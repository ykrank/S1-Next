package me.ykrank.s1next.widget.net

import android.content.Context
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.HttpDnsService
import com.alibaba.sdk.android.httpdns.RequestIpType
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.hostcheck.BaseDns
import com.github.ykrank.androidtools.widget.hostcheck.BaseHostUrl
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.data.api.Api
import java.net.InetAddress
import java.net.UnknownHostException


class AppDns(context: Context, baseHostUrl: BaseHostUrl) : BaseDns(baseHostUrl) {

    private val httpDns: HttpDnsService? = if (BuildConfig.HTTP_DNS_ID.isEmpty() ||
        BuildConfig.HTTP_DNS_SECRET.isEmpty()
    ) null
    else HttpDns.getService(context, BuildConfig.HTTP_DNS_ID, BuildConfig.HTTP_DNS_SECRET)//httpdns 解析服务

    private val hosts = listOf(
        "img.saraba1st.com",
        "p.sda1.dev",
    ) + Api.HOST_LIST

    private var hostIpMap: MutableMap<String, List<InetAddress>> = mutableMapOf()

    override fun lookup(hostname: String): List<InetAddress> {
        if (hosts.contains(hostname)) {
            var exception: Exception? = null
            var address: List<InetAddress>? = null
            try {
                address = super.lookup(hostname)
            } catch (e: Exception) {
                exception = e
            }

            if (address.isNullOrEmpty()) {
                var hostIp = hostIpMap[hostname]
                if (hostIp == null) {
                    val httpDnsResult =
                        httpDns?.getHttpDnsResultForHostSync(hostname, RequestIpType.auto)
                    hostIp = ((httpDnsResult?.ips ?: arrayOf()) + (httpDnsResult?.ipv6s
                        ?: arrayOf())).flatMap {
                        InetAddress.getAllByName(it).toList()
                    }
                    if (hostIp.isNotEmpty()) {
                        hostIpMap[hostname] = hostIp
                        L.d("HttpDns ip: $hostIp")
                    }
                }
                if (hostIp.isNotEmpty()) {
                    return hostIp
                } else {
                    if (exception == null) {
                        throw UnknownHostException("Broken system behaviour for dns lookup of $hostname and http dns")
                    }
                    throw exception
                }
            } else {
                return address
            }

        }
        return super.lookup(hostname)
    }
}