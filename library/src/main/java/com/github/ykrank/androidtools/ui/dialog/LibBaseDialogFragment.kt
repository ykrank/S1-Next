package com.github.ykrank.androidtools.ui.dialog

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.DialogFragment
import android.widget.Toast
import com.github.ykrank.androidtools.ui.UiGlobalData
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegate
import com.github.ykrank.androidtools.util.LeaksUtil
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.github.ykrank.androidtools.widget.track.event.page.FragmentEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.FragmentStartEvent
import java.lang.ref.WeakReference

/**
 * Created by ykrank on 2016/12/28.
 */

abstract class LibBaseDialogFragment : androidx.fragment.app.DialogFragment() {
    val trackAgent: DataTrackAgent? = UiGlobalData.provider?.trackAgent

    protected var mRetrySnackbar: WeakReference<Snackbar>? = null

    //Dialog null in onDestroyView, so save in onStop
    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        trackAgent?.post(FragmentStartEvent(this))
    }

    override fun onPause() {
        trackAgent?.post(FragmentEndEvent(this))
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        mDialog = dialog
    }

    override fun onDestroy() {
        mDialog?.apply {
            LeaksUtil.clearDialogLeaks(this)
        }
        mDialog = null
        super.onDestroy()
    }

    /**
     * @see CoordinatorLayoutAnchorDelegate#showToastText
     */
    protected fun showToastText(text: CharSequence?, length: Int = Toast.LENGTH_SHORT) {
        text?.let {
            val act = activity as CoordinatorLayoutAnchorDelegate?
            if (act == null) {
                UiGlobalData.toast.invoke(text, length)
            } else {
                act.showToastText(it, length)
            }
        }
    }
}
