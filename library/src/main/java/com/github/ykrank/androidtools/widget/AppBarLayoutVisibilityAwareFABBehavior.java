package com.github.ykrank.androidtools.widget;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Hide/show the {@link FloatingActionButton} when the dependent {@link AppBarLayout}
 * hides/shows.
 */
public final class AppBarLayoutVisibilityAwareFABBehavior extends FloatingActionButton.Behavior {

    public AppBarLayoutVisibilityAwareFABBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout || super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            if (dependency.getBottom() == 0 && child.getVisibility() == View.VISIBLE) {
                child.hide();
                return true;
            } else if (dependency.getBottom() == dependency.getHeight() && child.getVisibility() != View.VISIBLE) {
                // update the translation Y
                child.setTranslationY(getFabTranslationYForSnackbar(parent, child));
                child.show();
                return true;
            }
            return false;
        } else {
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }

    /**
     * See android.support.design.widget.FloatingActionButton.Behavior#getFabTranslationYForSnackbar(CoordinatorLayout, FloatingActionButton)
     */
    private float getFabTranslationYForSnackbar(CoordinatorLayout parent, FloatingActionButton fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout) {
                minOffset = Math.min(minOffset, view.getTranslationY() - view.getHeight());
            }
        }

        return minOffset;
    }
}
