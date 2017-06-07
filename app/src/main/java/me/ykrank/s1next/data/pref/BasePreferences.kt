package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.BoolRes
import android.support.annotation.StringRes
import android.text.TextUtils

/**
 * A base class wraps [SharedPreferences].
 */
abstract class BasePreferences(context: Context, val mSharedPreferences: SharedPreferences) {

    val mContext: Context = context.applicationContext

    fun getPrefString(@StringRes keyResId: Int, @StringRes defValueResId: Int): String {
        return getPrefString(keyResId, mContext.getString(defValueResId))
    }

    fun getPrefString(@StringRes keyResId: Int, defValue: String): String {
        val pref = mSharedPreferences.getString(mContext.getString(keyResId), defValue)
        if (TextUtils.isEmpty(pref)) {
            return defValue
        }
        return pref
    }

    fun getPrefBoolean(@StringRes keyResId: Int, @BoolRes defValueResId: Int): Boolean {
        return getPrefBoolean(keyResId, mContext.resources.getBoolean(defValueResId))
    }

    fun getPrefBoolean(@StringRes keyResId: Int, defValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(mContext.getString(keyResId), defValue)
    }

    fun putPrefString(@StringRes keyResId: Int, defValue: String) {
        mSharedPreferences.edit().putString(mContext.getString(keyResId), defValue).apply()
    }

    fun putPrefBoolean(@StringRes keyResId: Int, defValue: Boolean) {
        mSharedPreferences.edit().putBoolean(mContext.getString(keyResId), defValue).apply()
    }
}
