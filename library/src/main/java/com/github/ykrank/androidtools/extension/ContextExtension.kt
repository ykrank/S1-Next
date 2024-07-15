package com.github.ykrank.androidtools.extension

import android.content.Context
import android.widget.Toast
import com.github.ykrank.androidtools.util.LooperUtil
import com.github.ykrank.androidtools.util.RxJavaUtil

/**
 * Created by ykrank on 2017/6/4.
 */
fun Context.toast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    if (message != null) {
        if (LooperUtil.isOnMainThread) {
            Toast.makeText(this, message, duration).show()
        } else {
            LooperUtil.workInMainThread {
                Toast.makeText(this, message, duration).show()
            }
        }
    }
}

fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(this.getString(message), duration)
}