package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.event.FontSizeChangeEvent;
import me.ykrank.s1next.data.event.ThemeChangeEvent;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.dialog.ThreadGoDialogFragment;
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate;
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegateImpl;
import me.ykrank.s1next.view.internal.DrawerLayoutDelegate;
import me.ykrank.s1next.view.internal.DrawerLayoutDelegateConcrete;
import me.ykrank.s1next.view.internal.ToolbarDelegate;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.page.ActivityEndEvent;
import me.ykrank.s1next.widget.track.event.page.ActivityStartEvent;

/**
 * A base Activity which includes the Toolbar
 * and navigation drawer amongst others.
 * Also changes theme depends on settings.
 */
public abstract class BaseActivity extends OriginActivity
        implements CoordinatorLayoutAnchorDelegate {

    protected static final int REQUEST_CODE_MESSAGE_IF_SUCCESS = 0;
    private static final String EXTRA_MESSAGE = "message";

    @Inject
    EventBus mEventBus;

    @Inject
    User mUser;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    @Inject
    DataTrackAgent trackAgent;

    private ToolbarDelegate mToolbarDelegate;

    private DrawerLayoutDelegate mDrawerLayoutDelegate;
    private boolean mDrawerIndicatorEnabled = true;

    private CoordinatorLayoutAnchorDelegate mCoordinatorLayoutAnchorDelegate;
    @Nullable
    private WeakReference<Snackbar> mSnackbar;

    private Disposable mRecreateDisposable;


    /**
     * @see #setResultMessage(Activity, CharSequence)
     * @see #onActivityResult(int, int, Intent)
     */
    static void startActivityForResultMessage(Activity activity, Intent intent) {
        activity.startActivityForResult(intent, REQUEST_CODE_MESSAGE_IF_SUCCESS);
    }

    /**
     * Sets result message to {@link Activity} in order to show a short {@link Snackbar}
     * for this message during {@link #onActivityResult(int, int, Intent)}.
     *
     * @param message The message to show.
     * @see #startActivityForResultMessage(Activity, Intent)
     * @see #onActivityResult(int, int, Intent)
     */
    public static void setResultMessage(Activity activity, CharSequence message) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE, message);
        activity.setResult(Activity.RESULT_OK, intent);
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        App.getPrefComponent().inject(this);
        // change the theme depends on preference
        if (!mThemeManager.isDefaultTheme()) {
            if (isTranslucent()) {
                setTheme(mThemeManager.getThemeTranslucentStyle());
            } else {
                setTheme(mThemeManager.getThemeStyle());
            }
        }

        super.onCreate(savedInstanceState);

        mRecreateDisposable = mEventBus.get()
                .filter(o -> (o instanceof ThemeChangeEvent || o instanceof FontSizeChangeEvent))
                .subscribe(o -> {
                    getWindow().setWindowAnimations(R.style.Animation_Recreate);
                    recreate();
                });
    }

    public boolean isTranslucent() {
        return false;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mToolbarDelegate == null || !mToolbarDelegate.setTitle(title)) {
            super.setTitle(title);
        }
    }

    @Override
    @CallSuper
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
        setupCoordinatorLayoutAnchorDelegate();
    }

    @Override
    @CallSuper
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupToolbar();
        setupCoordinatorLayoutAnchorDelegate();
    }

    @Override
    @CallSuper
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackAgent.post(new ActivityStartEvent(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        trackAgent.post(new ActivityEndEvent(this));
    }

    @Override
    protected void onDestroy() {
        RxJavaUtil.disposeIfNotNull(mRecreateDisposable);
        if (mDrawerLayoutDelegate != null) {
            mDrawerLayoutDelegate.onDestroy();
            mDrawerLayoutDelegate = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // we show thread go menu only if this Activity has drawer
        if (mDrawerLayoutDelegate != null) {
            getMenuInflater().inflate(R.menu.thread_go, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event.
        if (mDrawerLayoutDelegate != null && mDrawerLayoutDelegate.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                // According to https://developer.android.com/design/patterns/navigation.html
                // we should navigate to its hierarchical parent of the current screen.
                // But the hierarchical logical is too complex in our app (sub forum, link redirection),
                // so we use finish() to close the current Activity.
                // looks the newest Google Play does the same way
                finish();

                return true;
            case R.id.menu_thread_go:
                new ThreadGoDialogFragment().show(getSupportFragmentManager(),
                        ThreadGoDialogFragment.TAG);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //sometime java.lang.IllegalStateException·Can not perform this action after onSaveInstanceState
        try {
            super.onBackPressed();
        } catch (Throwable throwable) {
            L.report(throwable);
        }
    }

    @Override
    @CallSuper
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerLayoutDelegate != null) {
            mDrawerLayoutDelegate.onConfigurationChanged(newConfig);
        }
    }

    /**
     * @see #startActivityForResultMessage(Activity, Intent)
     * @see #setResultMessage(Activity, CharSequence)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MESSAGE_IF_SUCCESS) {
            if (resultCode == Activity.RESULT_OK) {
                // We can't use #showShortText(String) because #onActivityResult(int, int, Intent)
                // is always invoked when current app is running in the foreground (so we are
                // unable to show a Toast if our app is running in the background).
                showShortSnackbar(data.getStringExtra(EXTRA_MESSAGE));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            mToolbarDelegate = new ToolbarDelegate(this, toolbar);
        }
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    final void setupNavCrossIcon() {
        mToolbarDelegate.setupNavCrossIcon();
    }

    final Optional<Toolbar> getToolbar() {
        if (mToolbarDelegate == null) {
            return Optional.absent();
        } else {
            return Optional.of(mToolbarDelegate.getToolbar());
        }
    }

    private void setupDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            mDrawerLayoutDelegate = new DrawerLayoutDelegateConcrete(this, drawerLayout,
                    (NavigationView) findViewById(R.id.navigation_view));
            mDrawerLayoutDelegate.setDrawerIndicatorEnabled(mDrawerIndicatorEnabled);
            mDrawerLayoutDelegate.onPostCreate();
        }
    }

    /**
     * Calls this method before {@link #onPostCreate(Bundle)}
     * otherwise it doesn't works.
     */
    final void disableDrawerIndicator() {
        mDrawerIndicatorEnabled = false;
    }

    private void setupCoordinatorLayoutAnchorDelegate() {
        mCoordinatorLayoutAnchorDelegate = new CoordinatorLayoutAnchorDelegateImpl(
                (CoordinatorLayout) Preconditions.checkNotNull(findViewById(R.id.coordinator_layout)));
    }

    @Override
    public final void setupFloatingActionButton(@DrawableRes int resId, View.OnClickListener onClickListener) {
        mCoordinatorLayoutAnchorDelegate.setupFloatingActionButton(resId, onClickListener);
    }

    public final void showShortToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public final void showShortToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public final Optional<Snackbar> showShortText(CharSequence text) {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate.showShortText(text));
    }

    @Override
    public final Optional<Snackbar> showShortSnackbar(@StringRes int resId) {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate.showShortSnackbar(resId));
    }

    @Override
    public final Optional<Snackbar> showShortSnackbar(CharSequence text) {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate.showShortSnackbar(text));
    }

    @Override
    public final Optional<Snackbar> showLongSnackbar(@StringRes int resId) {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate.showLongSnackbar(resId));
    }

    @Override
    public final Optional<Snackbar> showLongSnackbarIfVisible(CharSequence text, @StringRes int actionResId, View.OnClickListener onClickListener) {
        return saveSnackbarWeakReference(mCoordinatorLayoutAnchorDelegate.showShortSnackbar(text));
    }

    @Override
    public final void dismissSnackbarIfExist() {
        if (mSnackbar != null) {
            Snackbar snackbar = mSnackbar.get();
            if (snackbar != null && snackbar.isShownOrQueued()) {
                snackbar.dismiss();
            }
            mSnackbar = null;
        }
    }

    private Optional<Snackbar> saveSnackbarWeakReference(Optional<Snackbar> snackbar) {
        if (snackbar.isPresent()) {
            mSnackbar = new WeakReference<>(snackbar.get());
        }
        return snackbar;
    }

    public void replaceFragmentWithBackStack(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }
}
