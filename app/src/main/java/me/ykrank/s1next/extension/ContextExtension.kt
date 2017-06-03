package me.ykrank.s1next.extension

import android.content.Context
import android.widget.Toast

/**
 * Created by ykrank on 2017/6/4.
 */
fun Context.toast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    if (message != null) {
        Toast.makeText(this, message, duration).show()
    }
}