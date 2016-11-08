package me.ykrank.s1next.view.internal;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.common.base.Optional;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;

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
    public Optional<Snackbar> showShortText(CharSequence text) {
        if (mApp.isAppVisible()) {
            return showShortSnackbar(text);
        } else {
            Toast.makeText(mCoordinatorLayout.getContext().getApplicationContext(), text,
                    Toast.LENGTH_SHORT).show();
            return Optional.absent();
        }
    }

    @Override
    public Optional<Snackbar> showShortSnackbar(@StringRes int resId) {
        return showShortSnackbar(mCoordinatorLayout.getResources().getText(resId));
    }

    @Override
    public Optional<Snackbar> showShortSnackbar(CharSequence text) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
        return Optional.of(snackbar);
    }

    @Override
    public Optional<Snackbar> showLongSnackbar(@StringRes int resId){
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, resId, Snackbar.LENGTH_LONG);
        snackbar.show();
        return Optional.of(snackbar);
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

    @Override
    public void dismissSnackbarIfExist() {
        throw new UnsupportedOperationException();
    }
}
