package me.ykrank.s1next.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Process;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by AdminYkrank on 2016/4/19.
 */
public class ProcessUtil {

    /**
     * 获取当前进程名
     *
     * @param context
     * @param pid
     * @return
     */
    @Nullable
    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) return null;
        for (RunningAppProcessInfo runningApp : runningApps) {
            if (runningApp.pid == pid) return runningApp.processName;
        }
        return null;
    }

    /**
     * 判断Context是否在主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context, Process.myPid());
        String packageName = context.getPackageName();
        return TextUtils.equals(processName, packageName);
    }
}
