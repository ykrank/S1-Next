package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import me.ykrank.s1next.R

/**
 * A helper class retrieving the app api preferences from [SharedPreferences].
 */
class AppDataPreferencesImpl(context: Context, sharedPreferences: SharedPreferences)
    : BasePreferences(context, sharedPreferences), AppDataPreferences {

    override var appToken: String? by PreferenceDelegates.string(
            mContext.getString(R.string.pref_key_app_token), "")
}

interface AppDataPreferences {
    var appToken: String?
}

class AppDataPreferencesManager(private val mPreferencesProvider: AppDataPreferences) : AppDataPreferences by mPreferencesProvider