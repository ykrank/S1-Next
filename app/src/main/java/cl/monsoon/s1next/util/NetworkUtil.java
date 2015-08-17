package cl.monsoon.s1next.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkUtil {

    private NetworkUtil() {}

    /**
     * Check whether the Wi-Fi network connectivity exists and whtether it is possible to
     * establish connections and pass data.
     *
     * @return {@code true} if the Wi-FI network connectivity exists, {@code false} otherwise.
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }
}
