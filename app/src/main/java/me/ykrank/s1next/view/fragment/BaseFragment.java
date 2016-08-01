package me.ykrank.s1next.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.common.base.Optional;
import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;

import me.ykrank.s1next.App;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate;

public abstract class BaseFragment extends Fragment {

    protected UserValidator mUserValidator;

    protected CoordinatorLayoutAnchorDelegate mCoordinatorLayoutAnchorDelegate;
    @Nullable
    protected WeakReference<Snackbar> mRetrySnackbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCoordinatorLayoutAnchorDelegate = (CoordinatorLayoutAnchorDelegate) context;
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppComponent appComponent = App.getAppComponent(getContext());
        mUserValidator = appComponent.getUserValidator();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RefWatcher refWatcher = App.get().getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCoordinatorLayoutAnchorDelegate = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // see http://stackoverflow.com/a/9779971
        if (isVisible() && !isVisibleToUser) {
            // dismiss retry Snackbar when current Fragment hid
            // because this Snackbar is unrelated to other Fragments
            dismissRetrySnackbarIfExist();
        }
    }

    public void showRetrySnackbar(CharSequence text, View.OnClickListener onClickListener) {
        Optional<Snackbar> snackbar = mCoordinatorLayoutAnchorDelegate.showLongSnackbarIfVisible(
                text, R.string.snackbar_action_retry, onClickListener);
        if (snackbar.isPresent()) {
            mRetrySnackbar = new WeakReference<>(snackbar.get());
        }
    }
    
    protected void showShortSnackbar(CharSequence text){
        mCoordinatorLayoutAnchorDelegate.showShortSnackbar(text);
    }
    
    protected void showShortSnackbar(@StringRes int resId){
        mCoordinatorLayoutAnchorDelegate.showShortSnackbar(resId);
    }

    protected void showShortText(@StringRes int resId){
        mCoordinatorLayoutAnchorDelegate.showShortText(getString(resId));
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
