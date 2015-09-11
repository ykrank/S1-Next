package cl.monsoon.s1next.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.lang.ref.WeakReference;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.UserValidator;
import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.databinding.FragmentBaseBinding;
import cl.monsoon.s1next.view.fragment.headless.DataRetainedFragment;
import cl.monsoon.s1next.view.internal.CoordinatorLayoutAnchorDelegate;
import cl.monsoon.s1next.view.internal.LoadingViewModelBindingDelegate;
import cl.monsoon.s1next.view.internal.LoadingViewModelBindingDelegateImpl;
import cl.monsoon.s1next.viewmodel.LoadingViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A base Fragment includes {@link SwipeRefreshLayout} to refresh when loading data.
 * Also wraps {@link retrofit.Retrofit} to load data asynchronously.
 * <p>
 * We must call {@link #destroyRetainedFragment()}) if used in {@link android.support.v4.view.ViewPager}
 * otherwise leads memory leak.
 *
 * @param <D> The data we want to load.
 */
public abstract class BaseFragment<D> extends RxFragment {

    /**
     * The serialization (saved instance state) Bundle key representing
     * current loading state.
     */
    private static final String STATE_LOADING_VIEW_MODEL = "loading_view_model";

    S1Service mS1Service;

    private LoadingViewModelBindingDelegate mLoadingViewModelBindingDelegate;
    private LoadingViewModel mLoadingViewModel;

    /**
     * We use retained Fragment to retain data when configuration changes.
     */
    private DataRetainedFragment<D> mDataRetainedFragment;

    private UserValidator mUserValidator;

    private WeakReference<Snackbar> mRetrySnackbar;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBaseBinding fragmentBaseBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_base, container, false);
        mLoadingViewModelBindingDelegate = new LoadingViewModelBindingDelegateImpl(
                fragmentBaseBinding);
        return fragmentBaseBinding.getRoot();
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.AppComponent appComponent = App.getAppComponent(getContext());
        mS1Service = appComponent.getS1Service();
        mUserValidator = appComponent.getUserValidator();

        mLoadingViewModelBindingDelegate.getSwipeRefreshLayout().setOnRefreshListener(
                this::startSwipeRefresh);
    }

    @Override
    @CallSuper
    @SuppressWarnings("unchecked")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicates that this Fragment would like to
        // influence the set of actions in the Toolbar.
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            mLoadingViewModel = new LoadingViewModel();
        } else {
            mLoadingViewModel = savedInstanceState.getParcelable(STATE_LOADING_VIEW_MODEL);
        }

        // because we can't retain Fragments that are nested in other Fragments
        // so we need to confirm this Fragment has unique tag in order to compose
        // a new unique tag for its retained Fragment.
        // Without this, we couldn't get its retained Fragment back.
        String dataRetainedFragmentTag = DataRetainedFragment.TAG + "_" +
                Preconditions.checkNotNull(getTag(), "Must add a tag to " + this + ".");
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(dataRetainedFragmentTag);
        if (fragment == null) {
            mDataRetainedFragment = new DataRetainedFragment<>();
            fragmentManager.beginTransaction().add(mDataRetainedFragment, dataRetainedFragmentTag)
                    .commitAllowingStateLoss();

            // start to load data because we start this Fragment the first time
            mLoadingViewModel.setLoading(LoadingViewModel.LOADING_FIRST_TIME);
        } else {
            mDataRetainedFragment = (DataRetainedFragment) fragment;

            // get data back from retained Fragment when configuration changes
            if (mDataRetainedFragment.data != null) {
                int loading = mLoadingViewModel.getLoading();
                onNext(mDataRetainedFragment.data);
                mLoadingViewModel.setLoading(loading);
            } else {
                if (!mDataRetainedFragment.stale) {
                    // start to load data because the retained Fragment was killed by system
                    // and we have no data to load
                    mLoadingViewModel.setLoading(LoadingViewModel.LOADING_FIRST_TIME);
                }
            }
        }
        mDataRetainedFragment.stale = true;

        mLoadingViewModelBindingDelegate.setLoadingViewModel(mLoadingViewModel);
        if (isLoading()) {
            load();
        }
    }

    @Override
    @CallSuper
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_base, menu);
    }

    @Override
    @CallSuper
    public void onPrepareOptionsMenu(Menu menu) {
        // Disables the refresh menu when loading data.
        menu.findItem(R.id.menu_refresh).setEnabled(!isLoading());
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                startSwipeRefresh();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_LOADING_VIEW_MODEL, mLoadingViewModel);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // see http://stackoverflow.com/a/9779971
        if (isVisible() && !isVisibleToUser) {
            // dismiss retry SnackBar when current Fragment hid
            dismissRetrySnackBar();
        }
    }

    /**
     * Whether we are loading data now.
     */
    final boolean isLoading() {
        return mLoadingViewModel.getLoading() != LoadingViewModel.LOADING_FINISH;
    }

    /**
     * Whether we are pulling up to refresh.
     */
    final boolean isPullUpToRefresh() {
        return mLoadingViewModel.getLoading() == LoadingViewModel.LOADING_PULL_UP_TO_REFRESH;
    }

    /**
     * Show refresh progress and start to load new data.
     */
    private void startSwipeRefresh() {
        mLoadingViewModel.setLoading(LoadingViewModel.LOADING_SWIPE_REFRESH);
        load();
    }

    /**
     * Disables {@link SwipeRefreshLayout} and start to load new data.
     * <p>
     * Subclass should add {@link android.widget.ProgressBar} to {@link android.support.v7.widget.RecyclerView}
     * by itself.
     */
    final void startPullToRefresh() {
        mLoadingViewModel.setLoading(LoadingViewModel.LOADING_PULL_UP_TO_REFRESH);
        load();
    }

    /**
     * Starts to load new data.
     * <p>
     * Subclass should implement {@link #getSourceObservable()}
     * in oder to provider its own data source {@link Observable}.
     */
    private void load() {
        dismissRetrySnackBar();
        getSourceObservable().compose(bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(mUserValidator::validateIntercept)
                .finallyDo(this::finallyDo)
                .subscribe(this::onNext, this::onError);
    }

    /**
     * Subclass should implement this in order to provider its
     * data source {@link Observable}.
     * <p>
     * The data source {@link Observable} often comes from network
     * or database.
     *
     * @return The data source {@link Observable}.
     */
    abstract Observable<D> getSourceObservable();

    /**
     * Called when a data was emitted from {@link #getSourceObservable()}.
     * <p>
     * Actually this method was only called once during loading (if no error occurs)
     * because we only emit data once from {@link #getSourceObservable()}.
     */
    @CallSuper
    void onNext(D data) {
        mDataRetainedFragment.data = data;
    }

    /**
     * A helper method consumes {@link Result}.
     * <p>
     * Sometimes we cannot get data if we have logged out or
     * have no permission to access this data.
     * This method is only used during {@link #onNext(Object)}.
     *
     * @param result The data's result we get.
     */
    final void consumeResult(Result result) {
        if (getUserVisibleHint()) {
            String message = result.getMessage();
            if (!TextUtils.isEmpty(message)) {
                showRetrySnackBar(message);
            }
        }
    }

    /**
     * Called when an error occurs during data loading.
     * <p>
     * This stops the {@link #getSourceObservable()} and it will not make
     * further calls to {@link #onNext(Object)}.
     */
    @CallSuper
    void onError(Throwable throwable) {
        if (getUserVisibleHint()) {
            showRetrySnackBar(throwable.getMessage());
        }
    }

    /**
     * Called if it will not make further calls to {@link #onNext(Object)}
     * or {@link #onError(Throwable)} occurred during data loading.
     */
    @CallSuper
    void finallyDo() {
        mLoadingViewModel.setLoading(LoadingViewModel.LOADING_FINISH);
    }

    private void showRetrySnackBar(String message) {
        Optional<Snackbar> snackbar = ((CoordinatorLayoutAnchorDelegate) getActivity())
                .showLongSnackbarIfVisible(message, R.string.snackbar_action_retry,
                        isPullUpToRefresh()
                                ? v -> {if (!isLoading()) startPullToRefresh();}
                                : v -> {if (!isLoading()) startSwipeRefresh();});
        if (snackbar.isPresent()) {
            mRetrySnackbar = new WeakReference<>(snackbar.get());
        }
    }

    private void dismissRetrySnackBar() {
        if (mRetrySnackbar != null) {
            Snackbar snackbar = mRetrySnackbar.get();
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }

    final RecyclerView getRecyclerView() {
        return mLoadingViewModelBindingDelegate.getRecyclerView();
    }

    /**
     * We must call this if used in {@link android.support.v4.view.ViewPager}
     * otherwise leads memory leak.
     */
    public final void destroyRetainedFragment() {
        if (mDataRetainedFragment != null) {
            getFragmentManager().beginTransaction().remove(mDataRetainedFragment).commit();
        }
    }
}
