package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.github.ykrank.androidtools.data.BasePreferences
import com.github.ykrank.androidtools.data.PreferenceDelegates

import me.ykrank.s1next.R

/**
 * A helper class for retrieving the general preferences from [SharedPreferences].
 */
class GeneralPreferencesImpl(context: Context, sharedPreferences: SharedPreferences) : BasePreferences(context, sharedPreferences), GeneralPreferences {

    override var themeIndex: Int by PreferenceDelegates.int(
            R.string.pref_key_theme, R.string.pref_theme_default_value)

    override var darkThemeIndex: Int by PreferenceDelegates.int(
        R.string.pref_key_dark_theme, R.string.pref_dark_theme_default_value)

    override val fontScale: Float
        get() = getPrefString(R.string.pref_key_font_size, R.string.pref_font_size_default_value)?.toFloat()?:1.0f

    override val isSignatureEnabled: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_signature, R.bool.pref_signature_default_value)

    override var isPostSelectable: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_post_selectable, R.bool.pref_post_selectable_default_value)

    override var isQuickSideBarEnable: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_quick_side_bar_enable, R.bool.pref_quick_side_bar_enable_default_value)

    override var isPostDisableSticky: Boolean by PreferenceDelegates.bool(
            R.string.pref_key_post_disable_sticky, R.bool.pref_post_disable_sticky_default_value)
}

interface GeneralPreferences {
    var themeIndex: Int
    var darkThemeIndex: Int
    val fontScale: Float
    val isSignatureEnabled: Boolean
    var isPostSelectable: Boolean
    var isQuickSideBarEnable: Boolean
    var isPostDisableSticky:Boolean
}

class GeneralPreferencesManager(private val mPreferencesProvider: GeneralPreferences) : GeneralPreferences by mPreferencesProvider