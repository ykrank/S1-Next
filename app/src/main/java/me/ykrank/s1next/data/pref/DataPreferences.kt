package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences

import me.ykrank.s1next.R

class DataPreferencesImpl(context: Context, sharedPreferences: SharedPreferences)
    : BasePreferences(context, sharedPreferences), DataPreferences {

    override var hasNewPm: Boolean
        get() = getPrefBoolean(R.string.pref_key_has_new_pm, false)
        set(value) = putPrefBoolean(R.string.pref_key_has_new_pm, value)

    override var hasNewNotice: Boolean
        get() = getPrefBoolean(R.string.pref_key_has_new_notice, false)
        set(value) = putPrefBoolean(R.string.pref_key_has_new_notice, value)
}

interface DataPreferences {
    var hasNewPm: Boolean
    var hasNewNotice: Boolean
}

class DataPreferencesManager(private val mPreferencesProvider: DataPreferences) : DataPreferences by mPreferencesProvider