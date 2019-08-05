package me.ykrank.s1next.widget.net

import android.content.Context
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.HttpDnsService
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.hostcheck.BaseDns
import com.github.ykrank.androidtools.widget.hostcheck.BaseHostUrl
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.data.api.Api
import java.net.InetAddress
import java.net.UnknownHostException


class AppDns(context: Context, baseHostUrl: BaseHostUrl) : BaseDns(baseHostUrl) {

    private val httpDns: HttpDnsService = HttpDns.getService(context, BuildConfig.HTTP_DNS_ID, BuildConfig.HTTP_DNS_SECRET)//httpdns 解析服务

    private var hostIp:String? = null

    override fun lookup(hostname: String): List<InetAddress> {
        if (Api.BASE_HOST == hostname) {
            var exception: Exception? = null
            var address: List<InetAddress>? = null
            try {
                address = super.lookup(hostname)
            } catch (e: Exception) {
                exception = e
            }

            if (address.isNullOrEmpty()) {
                if (hostIp == null){
                    hostIp = httpDns.getIpByHostAsync(hostname)
                    if (hostIp != null) {
                        L.d("HttpDns ip: $hostIp")
                    }
                }
                val ip = hostIp
                if (ip != null) {
                    return InetAddress.getAllByName(ip).toList()
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