package cl.monsoon.s1next.view.internal;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;

/**
 * This class represents a delegate which you can use to
 * add {@link FloatingActionButton} or {@link Snackbar}
 * to {@link CoordinatorLayout} with Anchor.
 */
public final class CoordinatorLayoutAnchorDelegate {

    private final App mApp;

    private final CoordinatorLayout mCoordinatorLayout;

    public CoordinatorLayoutAnchorDelegate(CoordinatorLayout coordinatorLayout) {
        this.mCoordinatorLayout = coordinatorLayout;
        mApp = (App) coordinatorLayout.getContext().getApplicationContext();
    }

    public void setupFloatingActionButton(@DrawableRes int resId, View.OnClickListener onClickListener) {
        FloatingActionButton floatingActionButton = (FloatingActionButton) LayoutInflater.from(
                mCoordinatorLayout.getContext()).inflate(R.layout.floating_action_button,
                mCoordinatorLayout, false);
        mCoordinatorLayout.addView(floatingActionButton);

        floatingActionButton.setOnClickListener(onClickListener);
        floatingActionButton.setImageResource(resId);
    }

    /**
     * Show a long {@link Snackbar} if current {@link android.app.Activity} is visible,
     * otherwise show a long {@link android.widget.Toast}.
     *
     * @param text The text to show.
     */
    public void showLongText(CharSequence text) {
        if (mApp.isAppVisible()) {
            Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(mCoordinatorLayout.getContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show a {@link Snackbar} if current {@link android.app.Activity} is visible.
     *
     * @param text            The text to show.
     * @param actionResId     The action string resource to display.
     * @param onClickListener Callback to be invoked when the action is clicked.
     * @return The displayed {@link Snackbar} if current {@link android.app.Activity} is visible,
     * otherwise {@code null}.
     */
    public Snackbar showLongSnackbarIfVisible(CharSequence text, @StringRes int actionResId, View.OnClickListener onClickListener) {
        if (mApp.isAppVisible()) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_LONG);
            snackbar.setAction(actionResId, onClickListener);
            snackbar.show();
            return snackbar;
        }
        return null;
    }

    /**
     * Show a short {@link Snackbar}.
     *
     * @param resId The resource id of the string resource to show for {@link Snackbar}.
     */
    public void showShortSnackbar(@StringRes int resId) {
        Snackbar.make(mCoordinatorLayout, resId, Snackbar.LENGTH_SHORT).show();
    }
}
