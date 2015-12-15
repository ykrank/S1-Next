package cl.monsoon.s1next.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public final class NetworkUtil {

    private NetworkUtil() {}

    /**
     * Checks whether the Wi-Fi network connectivity exists and whether it is possible to
     * establish connections and pass data.
     *
     * @return {@code true} if the Wi-FI network connectivity exists, {@code false} otherwise.
     */
    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            if (networks != null) {
                for (Network network : connectivityManager.getAllNetworks()) {
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                    if (networkInfo != null &&
                            networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                            && networkInfo.isConnected()) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI);
            return networkInfo != null && networkInfo.isConnected();
        }

        return false;
    }
}
