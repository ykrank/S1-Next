package me.ykrank.s1next.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.google.common.base.Optional;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.page.FragmentEndEvent;
import me.ykrank.s1next.widget.track.event.page.FragmentStartEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    @Inject
    DataTrackAgent trackAgent;

    protected CoordinatorLayoutAnchorDelegate mCoordinatorLayoutAnchorDelegate;
    @Nullable
    protected WeakReference<Snackbar> mRetrySnackbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCoordinatorLayoutAnchorDelegate = (CoordinatorLayoutAnchorDelegate) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new FragmentStartEvent(this));
    }

    @Override
    public void onPause() {
        trackAgent.post(new FragmentEndEvent(this));
        super.onPause();
    }

    public void showRetrySnackbar(CharSequence text, View.OnClickListener onClickListener) {
        Optional<Snackbar> snackbar = mCoordinatorLayoutAnchorDelegate.showLongSnackbarIfVisible(
                text, R.string.snackbar_action_retry, onClickListener);
        if (snackbar.isPresent()) {
            mRetrySnackbar = new WeakReference<>(snackbar.get());
        }
    }

    protected void showShortSnackbar(CharSequence text) {
        mCoordinatorLayoutAnchorDelegate.showShortSnackbar(text);
    }

    protected void showShortSnackbar(@StringRes int resId) {
        mCoordinatorLayoutAnchorDelegate.showShortSnackbar(resId);
    }

    protected void showShortText(@StringRes int resId) {
        mCoordinatorLayoutAnchorDelegate.showShortText(getString(resId));
    }

    protected void showLongSnackbar(@StringRes int resId) {
        mCoordinatorLayoutAnchorDelegate.showLongSnackbar(resId);
    }

    protected void dismissRetrySnackbarIfExist() {
        if (mRetrySnackbar != null) {
            Snackbar snackbar = mRetrySnackbar.get();
            if (snackbar != null && snackbar.isShownOrQueued()) {
                snackbar.dismiss();
            }
            mRetrySnackbar = null;
        }
    }
}
