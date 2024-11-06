package me.ykrank.s1next

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.leakcanary2.FlipperLeakEventListener
import com.facebook.flipper.plugins.leakcanary2.LeakCanary2FlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import leakcanary.LeakCanary

object PreApp {
    val networkFlipperPlugin = NetworkFlipperPlugin()

    fun onCreate(app: Application) {
        if (FlipperUtils.shouldEnableFlipper(app)) {
            /*
            set the flipper listener in leak canary config
            */
            LeakCanary.config = LeakCanary.config.run {
                copy(eventListeners = eventListeners + FlipperLeakEventListener())
            }

            SoLoader.init(app, false)
            val client = AndroidFlipperClient.getInstance(app)
            client.addPlugin(InspectorFlipperPlugin(app, DescriptorMapping.withDefaults()))
            client.addPlugin(networkFlipperPlugin)
            client.addPlugin(DatabasesFlipperPlugin(app))
            client.addPlugin(CrashReporterPlugin.getInstance())
            client.addPlugin(LeakCanary2FlipperPlugin())
            client.start()
        }
    }
}