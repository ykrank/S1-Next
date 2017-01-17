package me.ykrank.s1next.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.Wifi;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.util.NetworkUtil;

/**
 * A helper class for registering broadcast receiver to check whether Wi-Fi
 * is enabled when we need to download images.
 */
public final class WifiBroadcastReceiver {

    private final Context mContext;
    @Inject
    Wifi mWifi;
    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;
    private BroadcastReceiver mBroadcastReceiver;

    public WifiBroadcastReceiver(Context context) {
        this.mContext = context;
        App.getPrefComponent().inject(this);
    }

    public void registerIfNeeded() {
        Preconditions.checkState(mBroadcastReceiver == null);

        if (mDownloadPreferencesManager.needMonitorWifi()) {
            mWifi.setWifiEnabled(NetworkUtil.isWifiConnected(mContext));

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mWifi.setWifiEnabled(NetworkUtil.isWifiConnected(context));
                }
            };

            IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            mContext.registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    public void unregisterIfNeeded() {
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    /**
     * A marker interface that any {@link android.app.Activity}
     * implements this interface would be registered to monitor
     * wifi status (if needed) automatically.
     */
    public interface NeedMonitorWifi {
    }
}
