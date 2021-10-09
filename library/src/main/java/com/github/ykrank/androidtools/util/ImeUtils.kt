/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrank.androidtools.util

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import java.lang.reflect.Method

/**
 * Utility methods for working with the keyboard
 */
object ImeUtils {

    /**
     * @see InputMethodManager.showSoftInput
     * @param view
     * @param flags
     */
    @JvmOverloads
    fun showIme(view: View, flags: Int = InputMethodManager.SHOW_IMPLICIT) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(view, flags)
    }

    fun hideIme(view: View) {
        val imm = view.context.getSystemService(Context
                .INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Backports [TextView.setShowSoftInputOnFocus] to API 20 and below.
     *
     * @see TextView.setShowSoftInputOnFocus
     */
    fun setShowSoftInputOnFocus(textView: TextView, show: Boolean?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.showSoftInputOnFocus = show!!
        } else {
            try {
                val method: Method
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    method = TextView::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
                } else {
                    method = TextView::class.java.getMethod("setSoftInputShownOnFocus", Boolean::class.javaPrimitiveType)
                }
                method.isAccessible = true
                method.invoke(textView, show)
            } catch (e: Exception) {
                L.leaveMsg("Sdk Version:" + Build.VERSION.SDK_INT)
                L.report("Failed to invoke TextView#setShowSoftInputOnFocus(boolean).", e)
            }

        }
    }
}
