package com.github.ykrank.androidtools.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Process;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by AdminYkrank on 2016/4/19.
 * 进程工具
 */
public class ProcessUtil {

    @Nullable
    public static String getProcessName(Context context, int pid) {
        String processName = getProcessNameFast();
        if (processName == null) {
            L.e("could not get processName fast");
            processName = getProcessNameSnow(context, pid);
        }
        return processName;
    }

    /**
     * 获取当前进程名
     *
     * @param context Context
     * @param pid     process pid
     * @return process name
     */
    @Nullable
    public static String getProcessNameSnow(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) return null;
        for (RunningAppProcessInfo runningApp : runningApps) {
            if (runningApp.pid == pid) return runningApp.processName;
        }
        return null;
    }

    /**
     * 获取当前进程名。直接读取proc/$app_pid/cmdline
     *
     * @return process name
     * @throws IOException
     */
    @Nullable
    public static String getProcessNameFast() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + Process.myPid() + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            L.e(throwable);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 判断Context是否在主进程
     *
     * @param context Context
     * @return is main process?
     */
    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context, Process.myPid());
        String packageName = context.getPackageName();
        return TextUtils.equals(processName, packageName);
    }
}
