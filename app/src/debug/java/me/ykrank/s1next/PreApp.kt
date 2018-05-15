package me.ykrank.s1next

import android.app.Application
import com.facebook.stetho.Stetho
import com.github.ykrank.androidtools.util.ProcessUtil

object PreApp {

    fun onCreate(app: Application) {
        //如果不是主进程，不做多余的初始化
        if (!ProcessUtil.isMainProcess(app))
            return
        Stetho.initializeWithDefaults(app)
    }
}