package com.github.ykrank.androidtools.ui.internal

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.ykrank.androidtools.ui.UiGlobalData
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Optional


abstract class CoordinatorLayoutAnchorDelegateBaseImpl(private val mCoordinatorLayout: CoordinatorLayout) :
    CoordinatorLayoutAnchorDelegate {

    private val actLifeCallback = UiGlobalData.provider?.actLifeCallback

    override fun showToastText(text: CharSequence, length: Int): Optional<Snackbar> {
        if (actLifeCallback?.isAppVisible == true) {
            return showSnackbar(text)
        } else {
            Toast.makeText(mCoordinatorLayout.context.applicationContext, text,
                    Toast.LENGTH_SHORT).show()
            return Optional.absent()
        }
    }

    override fun showSnackbar(
        @StringRes resId: Int,
        duration: Int,
        @StringRes actionResId: Int?,
        onActionClickListener: View.OnClickListener?,
    ): Optional<Snackbar> {
        return showSnackbar(
            mCoordinatorLayout.resources.getText(resId),
            duration,
            actionResId,
            onActionClickListener
        )
    }

    override fun showSnackbar(
        text: CharSequence,
        duration: Int,
        @StringRes actionResId: Int?,
        onActionClickListener: View.OnClickListener?,
    ): Optional<Snackbar> {
        val snackbar = Snackbar.make(mCoordinatorLayout, text, duration)
        if (actionResId != null && onActionClickListener != null) {
            snackbar.setAction(actionResId, onActionClickListener)
        }
        val textView = snackbar.view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.maxLines = SNACK_BAR_MAX_LINE
        snackbar.show()
        return Optional.of(snackbar)
    }

    override fun showLongSnackbarIfVisible(text: CharSequence, @StringRes actionResId: Int, onClickListener: View.OnClickListener): Optional<Snackbar> {
        if (actLifeCallback?.isAppVisible == true) {
            val snackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_LONG)
            val textView = snackbar.view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
            textView.maxLines = SNACK_BAR_MAX_LINE
            snackbar.setAction(actionResId, onClickListener)
            snackbar.show()
            return Optional.of(snackbar)
        }
        return Optional.absent()
    }

    override fun dismissSnackbarIfExist() {
        throw UnsupportedOperationException()
    }

    companion object{
        const val SNACK_BAR_MAX_LINE = 5
    }
}
