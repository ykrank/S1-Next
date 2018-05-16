package me.ykrank.s1next

import android.app.Application
import com.facebook.stetho.Stetho

object PreApp {

    fun onCreate(app: Application) {
        Stetho.initializeWithDefaults(app)
    }
}