package com.github.ykrank.androidtools.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegate
import com.github.ykrank.androidtools.ui.internal.DrawerLayoutDelegate
import com.github.ykrank.androidtools.ui.internal.DrawerLayoutOp
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.track.event.page.ActivityEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.ActivityStartEvent
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Optional
import java.lang.ref.WeakReference

/**
 * Created by ykrank on 2017/10/27.
 */
abstract class LibBaseActivity : AppCompatActivity(), CoordinatorLayoutAnchorDelegate, DrawerLayoutOp {

    private var mCoordinatorLayoutAnchorDelegate: CoordinatorLayoutAnchorDelegate? = null
    private var mDrawerLayoutDelegate: DrawerLayoutDelegate? = null
    private var mSnackbar: WeakReference<Snackbar>? = null
    protected open val mDrawerIndicatorEnabled = true

    @CallSuper
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupCoordinatorLayoutAnchorDelegate()
    }

    @CallSuper
    override fun setContentView(view: View?) {
        super.setContentView(view)
        setupCoordinatorLayoutAnchorDelegate()
    }

    @CallSuper
    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setupCoordinatorLayoutAnchorDelegate()
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerLayoutDelegate = findDrawerLayoutDelegate()
        mDrawerLayoutDelegate?.setDrawerIndicatorEnabled(mDrawerIndicatorEnabled)
        mDrawerLayoutDelegate?.onPostCreate()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        UiGlobalData.provider?.trackAgent?.post(ActivityStartEvent(this))
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        UiGlobalData.provider?.trackAgent?.post(ActivityEndEvent(this))
    }

    @CallSuper
    override fun onDestroy() {
        mDrawerLayoutDelegate?.onDestroy()
        mDrawerLayoutDelegate = null

        super.onDestroy()
    }

    open fun findDrawerLayoutDelegate(): DrawerLayoutDelegate? {
        return null
    }

    override fun openDrawer() {
        mDrawerLayoutDelegate?.openDrawer()
    }

    private fun setupCoordinatorLayoutAnchorDelegate() {
        mCoordinatorLayoutAnchorDelegate = findCoordinatorLayoutAnchorDelegate()
    }

    open fun findCoordinatorLayoutAnchorDelegate(): CoordinatorLayoutAnchorDelegate? {
        return null
    }

    override fun setupFloatingActionButton(@DrawableRes resId: Int, onClickListener: View.OnClickListener) {
        mCoordinatorLayoutAnchorDelegate?.setupFloatingActionButton(resId, onClickListener)
    }

    fun showShortToast(text: CharSequence) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun showShortToast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    override fun showToastText(text: CharSequence, length: Int): Optional<Snackbar> {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate?.showToastText(text))
    }

    override fun showSnackbar(@StringRes resId: Int, duration: Int): Optional<Snackbar> {
        return saveSnackbarWeakReference(
            mCoordinatorLayoutAnchorDelegate?.showSnackbar(
                resId,
                duration
            )
        )
    }

    override fun showSnackbar(text: CharSequence, duration: Int): Optional<Snackbar> {
        return saveSnackbarWeakReference(
            mCoordinatorLayoutAnchorDelegate?.showSnackbar(
                text,
                duration
            )
        )
    }

    override fun showLongSnackbarIfVisible(text: CharSequence, @StringRes actionResId: Int, onClickListener: View.OnClickListener): Optional<Snackbar> {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate?.showSnackbar(text))
    }

    override fun dismissSnackbarIfExist() {
        mSnackbar?.let {
            val snackbar = it.get()
            if (snackbar != null && snackbar.isShownOrQueued) {
                snackbar.dismiss()
            }
            mSnackbar = null
        }
    }

    private fun saveSnackbarWeakReference(snackbar: Optional<Snackbar>?): Optional<Snackbar> {
        if (snackbar == null) {
            return Optional.absent()
        }
        if (snackbar.isPresent) {
            mSnackbar = WeakReference(snackbar.get())
        }
        return snackbar
    }

    protected fun leavePageMsg(msg: String) {
        L.leaveMsg(msg)
    }

}