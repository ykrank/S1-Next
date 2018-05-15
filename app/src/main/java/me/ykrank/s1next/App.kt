package me.ykrank.s1next

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.StrictMode
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.github.ykrank.androidtools.AppDataProvider
import com.github.ykrank.androidtools.GlobalData
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.ui.RProvider
import com.github.ykrank.androidtools.ui.UiDataProvider
import com.github.ykrank.androidtools.ui.UiGlobalData
import com.github.ykrank.androidtools.util.*
import com.github.ykrank.androidtools.widget.net.WifiActivityLifecycleCallbacks
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import me.ykrank.s1next.data.db.DbModule
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.data.pref.PrefModule
import me.ykrank.s1next.util.BuglyUtils
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.widget.AppActivityLifecycleCallbacks

class App : MultiDexApplication() {

    init {
        sApp = this
    }

    private lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    private lateinit var mAppComponent: AppComponent

    private lateinit var mPreAppComponent: PreAppComponent

    private lateinit var mAppActivityLifecycleCallbacks: AppActivityLifecycleCallbacks

    var resourceContext: Context? = null

    lateinit var refWatcher: RefWatcher
        private set

    val trackAgent: DataTrackAgent
        get() = mPreAppComponent.dataTrackAgent

    val isAppVisible: Boolean
        get() = mAppActivityLifecycleCallbacks.isAppVisible

    override fun attachBaseContext(base: Context) {
        mPreAppComponent = DaggerPreAppComponent.builder()
                .preAppModule(PreAppModule(this))
                .prefModule(PrefModule(base))
                .build()
        mGeneralPreferencesManager = mPreAppComponent.generalPreferencesManager
        super.attachBaseContext(ResourceUtil.setScaledDensity(base, mGeneralPreferencesManager.fontScale))
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        // enable StrictMode when debug
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }

        mPreAppComponent.dataTrackAgent.init(this)
        GlobalData.init(object : AppDataProvider {
            override val errorParser: ErrorParser?
                get() = ErrorUtil
            override val logTag: String
                get() = LOG_TAG
            override val debug: Boolean
                get() = BuildConfig.DEBUG
            override val buildType: String
                get() = BuildConfig.BUILD_TYPE
            override val itemModelBRid: Int
                get() = BR.model
            override val recycleViewLoadingImgId: Int
                get() = R.drawable.loading
            override val recycleViewErrorImgId: Int
                get() = R.drawable.recycleview_error_symbol
            override val appR: Class<out Any>
                get() = R::class.java
        })
        refWatcher = LeaksUtil.install(this)
        L.init(this)
        BuglyUtils.init(this)

        mAppComponent = DaggerAppComponent.builder()
                .preAppComponent(mPreAppComponent)
                .buildTypeModule(BuildTypeModule(this))
                .appModule(AppModule())
                .dbModule(DbModule())
                .build()

        mAppActivityLifecycleCallbacks = AppActivityLifecycleCallbacks(this, mAppComponent.noticeCheckTask)
        registerActivityLifecycleCallbacks(mAppActivityLifecycleCallbacks)

        UiGlobalData.init(object : UiDataProvider {
            override val refWatcher: RefWatcher
                get() = this@App.refWatcher
            override val actLifeCallback: WifiActivityLifecycleCallbacks
                get() = mAppActivityLifecycleCallbacks
            override val trackAgent: DataTrackAgent
                get() = mPreAppComponent.dataTrackAgent
        }, object : RProvider {

        }, this::toast)

        L.l("App init")
        PreApp.onCreate(this)

        //如果不是主进程，不做多余的初始化
        if (!ProcessUtil.isMainProcess(this))
            return

        //enable vector drawable
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        L.l("App onConfigurationChanged")

        //如果不是主进程，不做多余的初始化
        if (!ProcessUtil.isMainProcess(this))
            return
    }

    companion object {
        val LOG_TAG = "S1NextLog"

        private lateinit var sApp: App

        fun get(): App {
            return sApp
        }

        val appComponent: AppComponent
            get() = sApp.mAppComponent

        val preAppComponent: PreAppComponent
            get() = sApp.mPreAppComponent
    }
}
