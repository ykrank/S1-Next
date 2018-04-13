package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.github.ykrank.androidtools.data.BasePreferences
import com.github.ykrank.androidtools.data.PreferenceDelegates

import me.ykrank.s1next.R

class DataPreferencesImpl(context: Context, sharedPreferences: SharedPreferences)
    : BasePreferences(context, sharedPreferences), DataPreferences {

    override var hasNewPm: Boolean by PreferenceDelegates.bool(mContext.getString(R.string.pref_key_has_new_pm), false)

    override var hasNewNotice: Boolean by PreferenceDelegates.bool(mContext.getString(R.string.pref_key_has_new_notice), false)
}

interface DataPreferences {
    var hasNewPm: Boolean
    var hasNewNotice: Boolean
}

class DataPreferencesManager(private val mPreferencesProvider: DataPreferences) : DataPreferences by mPreferencesProvider