package cl.monsoon.s1next.util;

import android.content.Context;
import android.os.Build;

import org.apache.commons.lang3.StringUtils;

import cl.monsoon.s1next.R;

public final class DeviceUtil {

    private DeviceUtil() {}

    /**
     * Gets the string signature which is used for reply (append this to last line of the reply).
     */
    public static String getSignature(Context context) {
        return context.getString(R.string.signature, getDeviceNameWithVersion());
    }

    /**
     * Forked from https://stackoverflow.com/questions/1995439/get-android-phone-model-programmatically/26117427#answer-12707479
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
