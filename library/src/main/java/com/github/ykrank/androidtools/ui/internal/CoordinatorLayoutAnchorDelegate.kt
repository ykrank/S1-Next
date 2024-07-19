package com.github.ykrank.androidtools.ui.internal

import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Optional

/**
 * This class represents a delegate which you can use to
 * add [FloatingActionButton] or [Snackbar]
 * to [CoordinatorLayout] with Anchor.
 */
interface CoordinatorLayoutAnchorDelegate {

    fun setupFloatingActionButton(@DrawableRes resId: Int, onClickListener: View.OnClickListener)

    /**
     * Show a [Snackbar] if current [android.app.Activity] is visible,
     * otherwise show a [android.widget.Toast].
     *
     * @param text The text to show.
     * @param length Toast length
     * @return The displayed `Optional.of(snackbar)` if we use [Snackbar] to
     * show short text, otherwise the `Optional.absent()`.
     */
    fun showToastText(text: CharSequence, length: Int = Toast.LENGTH_SHORT): Optional<Snackbar>

    /**
     * Show a short [Snackbar].
     *
     * @param resId The resource id of the string resource to show for [Snackbar].
     * @return The displayed `Optional.of(snackbar)`.
     */
    fun showSnackbar(
        @StringRes resId: Int,
        duration: Int = Snackbar.LENGTH_SHORT,
        @StringRes actionResId: Int? = null,
        onActionClickListener: View.OnClickListener? = null,
    ): Optional<Snackbar>

    /**
     * Show a short [Snackbar].
     *
     * @param text text The text to show.
     * @return The displayed `Optional.of(snackbar)`.
     */
    fun showSnackbar(
        text: CharSequence,
        duration: Int = Snackbar.LENGTH_SHORT,
        @StringRes actionResId: Int? = null,
        onActionClickListener: View.OnClickListener? = null,
    ): Optional<Snackbar>

    /**
     * Show a [Snackbar] if current [android.app.Activity] is visible.
     *
     * @param text            The text to show.
     * @param actionResId     The action string resource to display.
     * @param onClickListener Callback to be invoked when the action is clicked.
     * @return The displayed `Optional.of(snackbar)` if current [android.app.Activity]
     * is visible, otherwise the `Optional.absent()`.
     */
    fun showLongSnackbarIfVisible(text: CharSequence, @StringRes actionResId: Int, onClickListener: View.OnClickListener): Optional<Snackbar>

    /**
     * Dismiss the [Snackbar] if [CoordinatorLayout] has Snackbar.
     */
    fun dismissSnackbarIfExist()
}
