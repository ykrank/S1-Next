package cl.monsoon.s1next.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;

public final class NetworkUtil {

    private NetworkUtil() {}

    /**
     * Check whether the Wi-Fi network connectivity exists and whtether it is possible to
     * establish connections and pass data.
     *
     * @return {@code true} if the Wi-FI network connectivity exists, {@code false} otherwise.
     */
    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : connectivityManager.getAllNetworks()) {
                if (isNetworkInfoConnected(connectivityManager.getNetworkInfo(network))) {
                    return true;
                }
            }
        } else {
            return isNetworkInfoConnected(connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI));
        }

        return false;
    }

    private static boolean isNetworkInfoConnected(@Nullable NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }
}
