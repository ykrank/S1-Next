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

package me.ykrank.s1next.util;

import android.content.Context;
import android.os.Build;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * Utility methods for working with the keyboard
 */
public class ImeUtils {

    private ImeUtils() {
    }

    public static void showIme(@NonNull View view) {
        showIme(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * @see InputMethodManager#showSoftInput(View, int) 
     * @param view
     * @param flags
     */
    public static void showIme(@NonNull View view, int flags) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, flags);
    }

    public static void hideIme(@NonNull View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Backports {@link TextView#setShowSoftInputOnFocus} to API 20 and below.
     *
     * @see TextView#setShowSoftInputOnFocus(boolean)
     */
    public static void setShowSoftInputOnFocus(TextView textView, Boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setShowSoftInputOnFocus(show);
        } else {
            try {
                Method method;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    method = TextView.class.getMethod("setShowSoftInputOnFocus", boolean.class);
                } else {
                    method = TextView.class.getMethod("setSoftInputShownOnFocus", boolean.class);
                }
                method.setAccessible(true);
                method.invoke(textView, show);
            } catch (Exception e) {
                L.leaveMsg("Sdk Version:"+Build.VERSION.SDK_INT);
                L.report("Failed to invoke TextView#setShowSoftInputOnFocus(boolean).", e);
            }
        }
    }
}
