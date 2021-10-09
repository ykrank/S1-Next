package com.github.ykrank.androidtools.util;

import android.os.Build;

import org.apache.commons.lang3.StringUtils;

public final class DeviceUtil {

    private DeviceUtil() {
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
