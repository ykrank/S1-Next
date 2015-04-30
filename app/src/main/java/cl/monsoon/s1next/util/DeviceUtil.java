package cl.monsoon.s1next.util;

import android.os.Build;

import org.apache.commons.lang3.StringUtils;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;

public final class DeviceUtil {

    private DeviceUtil() {

    }

    public static String getSignature() {
        return App.getContext().getString(R.string.signature, getDeviceNameWithVersion());
    }

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
