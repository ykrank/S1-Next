package me.ykrank.s1next.view.internal

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegateBaseImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.ykrank.s1next.R

class CoordinatorLayoutAnchorDelegateImpl(private val mCoordinatorLayout: CoordinatorLayout) :
    CoordinatorLayoutAnchorDelegateBaseImpl(mCoordinatorLayout) {

    override fun setupFloatingActionButton(
        @DrawableRes resId: Int,
        onClickListener: View.OnClickListener
    ) {
        val floatingActionButton = LayoutInflater.from(
            mCoordinatorLayout.context
        ).inflate(
            R.layout.floating_action_button,
            mCoordinatorLayout, false
        ) as FloatingActionButton
        mCoordinatorLayout.addView(floatingActionButton)

        floatingActionButton.setOnClickListener(onClickListener)
        floatingActionButton.setImageResource(resId)
    }
}
