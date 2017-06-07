package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences

import me.ykrank.s1next.R

/**
 * A helper class for retrieving the general preferences from [SharedPreferences].
 */
class GeneralPreferencesImpl(context: Context, sharedPreferences: SharedPreferences) : BasePreferences(context, sharedPreferences), GeneralPreferences {

    override var themeIndex: Int
        get() = getPrefString(R.string.pref_key_theme, R.string.pref_theme_default_value).toInt()
        /**
         * Commits theme preference change for settings.
         * @param value The new value for the theme preference.
         */
        set(value) = putPrefString(R.string.pref_key_theme, value.toString())

    override val fontScale: Float
        get() = getPrefString(R.string.pref_key_font_size, R.string.pref_font_size_default_value).toFloat()

    override val isSignatureEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_key_signature, R.bool.pref_signature_default_value)

    override var isPostSelectable: Boolean
        get() = getPrefBoolean(R.string.pref_key_post_selectable, R.bool.pref_post_selectable_default_value)
        set(selectable) = putPrefBoolean(R.string.pref_key_post_selectable, selectable)

    override var isQuickSideBarEnable: Boolean
        get() = getPrefBoolean(R.string.pref_key_quick_side_bar_enable,
                R.bool.pref_quick_side_bar_enable_default_value)
        set(enable) = putPrefBoolean(R.string.pref_key_quick_side_bar_enable, enable)
}

interface GeneralPreferences {
    var themeIndex: Int
    val fontScale: Float
    val isSignatureEnabled: Boolean
    var isPostSelectable: Boolean
    var isQuickSideBarEnable: Boolean
}

class GeneralPreferencesManager(private val mPreferencesProvider: GeneralPreferences) : GeneralPreferences by mPreferencesProvider