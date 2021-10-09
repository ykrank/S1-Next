package com.github.ykrank.androidtools.widget.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import com.google.common.base.Preconditions
import com.github.ykrank.androidtools.util.NetworkUtil

/**
 * A helper class for registering broadcast receiver to check whether Wi-Fi
 * is enabled when we need to download images.
 */
class WifiBroadcastReceiver(private val mContext: Context, private val wifiStateChangedCallback: ((Boolean) -> Unit)?) {
    private var mBroadcastReceiver: BroadcastReceiver? = null

    fun registerIfNeeded() {
        Preconditions.checkState(mBroadcastReceiver === null)

        wifiStateChangedCallback?.invoke(NetworkUtil.isWifiConnected(mContext))

        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                wifiStateChangedCallback?.invoke(NetworkUtil.isWifiConnected(context))
            }
        }

        val intentFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        mContext.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    fun unregisterIfNeeded() {
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBroadcastReceiver)
            mBroadcastReceiver = null
        }
    }

    /**
     * A marker interface that any [android.app.Activity]
     * implements this interface would be registered to monitor
     * wifi status (if needed) automatically.
     */
    interface NeedMonitorWifi
}
