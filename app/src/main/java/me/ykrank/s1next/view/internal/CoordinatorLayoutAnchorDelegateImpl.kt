package me.ykrank.s1next.view.internal

import android.support.annotation.DrawableRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegateBaseImpl
import me.ykrank.s1next.R

class CoordinatorLayoutAnchorDelegateImpl(private val mCoordinatorLayout: CoordinatorLayout) : CoordinatorLayoutAnchorDelegateBaseImpl(mCoordinatorLayout) {

    override fun setupFloatingActionButton(@DrawableRes resId: Int, onClickListener: View.OnClickListener) {
        val floatingActionButton = LayoutInflater.from(
                mCoordinatorLayout.context).inflate(R.layout.floating_action_button,
                mCoordinatorLayout, false) as FloatingActionButton
        mCoordinatorLayout.addView(floatingActionButton)

        floatingActionButton.setOnClickListener(onClickListener)
        floatingActionButton.setImageResource(resId)
    }
}
