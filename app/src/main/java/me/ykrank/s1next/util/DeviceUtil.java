package me.ykrank.s1next.util;

import android.content.Context;
import android.os.Build;

import org.apache.commons.lang3.StringUtils;

import me.ykrank.s1next.R;

public final class DeviceUtil {

    private DeviceUtil() {}

    /**
     * Gets the string signature which is used for reply (show in setting).
     */
    public static String getSignature(Context context) {
        return context.getString(R.string.signature, getDeviceNameWithVersion());
    }

    /**
     * Gets the string signature which is used for reply (append this to last line of the reply).
     */
    public static String getPostSignature(Context context) {
        return context.getString(R.string.signature_in_reply, getDeviceNameWithVersion());
    }

    /**
     * Forked from http://stackoverflow.com/a/12707479
     */
    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + StringUtils.SPACE + model;
        }
    }

    private static String getDeviceNameWithVersion() {
        return getDeviceName() + ',' + StringUtils.SPACE
                + "Android" + StringUtils.SPACE + Build.VERSION.RELEASE;
    }
}
