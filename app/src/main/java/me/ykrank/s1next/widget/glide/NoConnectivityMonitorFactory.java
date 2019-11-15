package me.ykrank.s1next.widget.glide;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.manager.ConnectivityMonitor;
import com.bumptech.glide.manager.ConnectivityMonitorFactory;

/**
 * 功能：不做网络监听，兼容华为平板
 */
public class NoConnectivityMonitorFactory implements ConnectivityMonitorFactory {
    /**
     * 太多广播，需要禁用glide网络监听功能
     *
     * @return
     */
    public static boolean needDisableNetCheck() {
        return isHuawei() || Build.VERSION.SDK_INT > Build.VERSION_CODES.O;
    }

    static boolean isHuawei() {
        String brand = Build.BRAND.toLowerCase();
        return brand.contains("huawei") || brand.contains("honor");
    }

    @NonNull
    @Override
    public ConnectivityMonitor build(@NonNull Context context, @NonNull ConnectivityMonitor.ConnectivityListener listener) {

        return new ConnectivityMonitor() {

            @Override
            public void onStart() {
                //不做处理
            }

            @Override
            public void onStop() {
//不做处理
            }

            @Override
            public void onDestroy() {
//不做处理
            }
        };
    }
}