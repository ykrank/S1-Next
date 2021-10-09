package com.github.ykrank.androidtools.extension

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.appcompat.view.SupportMenuInflater
import androidx.appcompat.widget.Toolbar

/**
 * 不依赖Activity的独立初始化Fragment布局中的Toolbar里的menu
 */
@SuppressLint("RestrictedApi")
fun androidx.fragment.app.Fragment.independentMenu(toolbar: Toolbar?) {
    if (toolbar != null) {
        onCreateOptionsMenu(toolbar.menu, SupportMenuInflater(context))
        toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
            return@setOnMenuItemClickListener true
        }
    }
}
