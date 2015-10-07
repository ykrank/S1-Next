package cl.monsoon.s1next.view.internal;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.common.base.Optional;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;

public final class CoordinatorLayoutAnchorDelegateImpl implements CoordinatorLayoutAnchorDelegate {

    private final App mApp;

    private final CoordinatorLayout mCoordinatorLayout;

    public CoordinatorLayoutAnchorDelegateImpl(CoordinatorLayout coordinatorLayout) {
        this.mCoordinatorLayout = coordinatorLayout;
        mApp = (App) coordinatorLayout.getContext().getApplicationContext();
    }

    @Override
    public void setupFloatingActionButton(@DrawableRes int resId, View.OnClickListener onClickListener) {
        FloatingActionButton floatingActionButton = (FloatingActionButton) LayoutInflater.from(
                mCoordinatorLayout.getContext()).inflate(R.layout.floating_action_button,
                mCoordinatorLayout, false);
        mCoordinatorLayout.addView(floatingActionButton);

        floatingActionButton.setOnClickListener(onClickListener);
        floatingActionButton.setImageResource(resId);
    }

    @Override
    public void showShortText(CharSequence text) {
        if (mApp.isAppVisible()) {
            Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mCoordinatorLayout.getContext().getApplicationContext(), text,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showShortSnackbar(@StringRes int resId) {
        Snackbar.make(mCoordinatorLayout, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public Optional<Snackbar> showLongSnackbarIfVisible(CharSequence text, @StringRes int actionResId, View.OnClickListener onClickListener) {
        if (mApp.isAppVisible()) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_LONG);
            snackbar.setAction(actionResId, onClickListener);
            snackbar.show();
            return Optional.of(snackbar);
        }
        return Optional.absent();
    }
}
