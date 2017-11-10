package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.BoolRes
import android.support.annotation.StringRes
import android.text.TextUtils
import com.github.ykrank.androidtools.extension.resBool
import com.github.ykrank.androidtools.extension.resStr
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A base class wraps [SharedPreferences].
 */
abstract class BasePreferences(context: Context, val preferences: SharedPreferences) {

    val mContext: Context = context.applicationContext

    fun getPrefString(@StringRes keyResId: Int, @StringRes defValueResId: Int): String {
        return getPrefString(keyResId, mContext.getString(defValueResId))
    }

    fun getPrefString(@StringRes keyResId: Int, defValue: String): String {
        val pref = preferences.getString(mContext.getString(keyResId), defValue)
        if (TextUtils.isEmpty(pref)) {
            return defValue
        }
        return pref
    }

    fun getPrefBoolean(@StringRes keyResId: Int, @BoolRes defValueResId: Int): Boolean {
        return getPrefBoolean(keyResId, mContext.resources.getBoolean(defValueResId))
    }

    fun getPrefBoolean(@StringRes keyResId: Int, defValue: Boolean): Boolean {
        return preferences.getBoolean(mContext.getString(keyResId), defValue)
    }

    fun putPrefString(@StringRes keyResId: Int, defValue: String) {
        preferences.edit().putString(mContext.getString(keyResId), defValue).apply()
    }

    fun putPrefBoolean(@StringRes keyResId: Int, defValue: Boolean) {
        preferences.edit().putBoolean(mContext.getString(keyResId), defValue).apply()
    }
}

object PreferenceDelegates {
    fun string(key: String? = null, defaultValue: String? = null): ReadWriteProperty<BasePreferences, String?> {
        return PrefString(key, defaultValue)
    }

    fun string(@StringRes key: Int? = null, @StringRes defaultValue: Int? = null): ReadWriteProperty<BasePreferences, String?> {
        return PrefResString(key, defaultValue)
    }

    fun bool(key: String? = null, defaultValue: Boolean): ReadWriteProperty<BasePreferences, Boolean> {
        return PrefBoolean(key, defaultValue)
    }

    fun bool(@StringRes key: Int? = null, @BoolRes defaultValue: Int): ReadWriteProperty<BasePreferences, Boolean> {
        return PrefResBoolean(key, defaultValue)
    }

    fun int(key: String? = null, defaultValue: Int): ReadWriteProperty<BasePreferences, Int> {
        return PrefInt(key, defaultValue)
    }

    fun int(@StringRes key: Int? = null, @StringRes defaultValue: Int): ReadWriteProperty<BasePreferences, Int> {
        return PrefResIntStr(key, defaultValue)
    }
}

class PrefString(private val key: String?, private val defaultValue: String?) : ReadWriteProperty<BasePreferences, String?> {
    override fun getValue(thisRef: BasePreferences, property: KProperty<*>): String? {
        return thisRef.preferences.getString(key ?: property.name, defaultValue)
    }

    override fun setValue(thisRef: BasePreferences, property: KProperty<*>, value: String?) {
        thisRef.preferences.edit().putString(key ?: property.name, value).apply()
    }
}

class PrefResString(@StringRes private val key: Int?, @StringRes private val defaultValue: Int?) : ReadWriteProperty<BasePreferences, String?> {
    override fun getValue(thisRef: BasePreferences, property: KProperty<*>): String? {
        return thisRef.preferences.getString(key?.resStr(thisRef.mContext) ?: property.name, defaultValue?.resStr(thisRef.mContext))
    }

    override fun setValue(thisRef: BasePreferences, property: KProperty<*>, value: String?) {
        thisRef.preferences.edit().putString(key?.resStr(thisRef.mContext) ?: property.name, value).apply()
    }
}

class PrefBoolean(private val key: String?, private val defaultValue: Boolean) : ReadWriteProperty<BasePreferences, Boolean> {
    override fun getValue(thisRef: BasePreferences, property: KProperty<*>): Boolean {
        return thisRef.preferences.getBoolean(key ?: property.name, defaultValue)
    }

    override fun setValue(thisRef: BasePreferences, property: KProperty<*>, value: Boolean) {
        thisRef.preferences.edit().putBoolean(key ?: property.name, value).apply()
    }
}

class PrefResBoolean(@StringRes private val key: Int?, @BoolRes private val defaultValue: Int) : ReadWriteProperty<BasePreferences, Boolean> {
    override fun getValue(thisRef: BasePreferences, property: KProperty<*>): Boolean {
        return thisRef.preferences.getBoolean(key?.resStr(thisRef.mContext) ?: property.name, defaultValue.resBool(thisRef.mContext))
    }

    override fun setValue(thisRef: BasePreferences, property: KProperty<*>, value: Boolean) {
        thisRef.preferences.edit().putBoolean(key?.resStr(thisRef.mContext) ?: property.name, value).apply()
    }
}

class PrefInt(private val key: String?, private val defaultValue: Int) : ReadWriteProperty<BasePreferences, Int> {
    override fun getValue(thisRef: BasePreferences, property: KProperty<*>): Int {
        return thisRef.preferences.getInt(key ?: property.name, defaultValue)
    }

    override fun setValue(thisRef: BasePreferences, property: KProperty<*>, value: Int) {
        thisRef.preferences.edit().putInt(key ?: property.name, value).apply()
    }
}

class PrefResIntStr(@StringRes private val key: Int?, @StringRes private val defaultValue: Int) : ReadWriteProperty<BasePreferences, Int> {
    override fun getValue(thisRef: BasePreferences, property: KProperty<*>): Int {
        return thisRef.preferences.getString(key?.resStr(thisRef.mContext) ?: property.name, defaultValue.resStr(thisRef.mContext)).toInt()
    }

    override fun setValue(thisRef: BasePreferences, property: KProperty<*>, value: Int) {
        thisRef.preferences.edit().putString(key?.resStr(thisRef.mContext) ?: property.name, value.toString()).apply()
    }
}