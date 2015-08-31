package cl.monsoon.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.event.FontSizeChangeEvent;
import cl.monsoon.s1next.data.event.ThemeChangeEvent;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.view.internal.CoordinatorLayoutAnchorDelegate;
import cl.monsoon.s1next.view.internal.CoordinatorLayoutAnchorDelegateImpl;
import cl.monsoon.s1next.view.internal.DrawerLayoutDelegate;
import cl.monsoon.s1next.view.internal.DrawerLayoutDelegateConcrete;
import cl.monsoon.s1next.view.internal.ToolbarDelegate;
import cl.monsoon.s1next.widget.EventBus;

/**
 * A base Activity which includes the Toolbar
 * and navigation drawer amongst others.
 * Also changes theme depends on settings.
 */
public abstract class BaseActivity extends RxAppCompatActivity
        implements CoordinatorLayoutAnchorDelegate {

    private static final int REQUEST_CODE_MESSAGE_IF_SUCCESS = 0;
    private static final String EXTRA_MESSAGE = "message";

    @Inject
    EventBus mEventBus;

    @Inject
    User mUser;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    private ToolbarDelegate mToolbarDelegate;

    private DrawerLayoutDelegate mDrawerLayoutDelegate;
    private boolean mDrawerIndicatorEnabled = true;

    private CoordinatorLayoutAnchorDelegate mCoordinatorLayoutAnchorDelegate;

    public static void startActivityForResultMessage(Activity activity, Intent intent) {
        activity.startActivityForResult(intent, REQUEST_CODE_MESSAGE_IF_SUCCESS);
    }

    public static void setResultMessage(Activity activity, CharSequence message) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE, message);
        activity.setResult(Activity.RESULT_OK, intent);
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent(this).inject(this);
        // change the theme depends on preference
        if (!mThemeManager.isDefaultTheme()) {
            setTheme(mThemeManager.getThemeStyle());
        }

        super.onCreate(savedInstanceState);

        mEventBus.get().compose(bindToLifecycle()).subscribe(o -> {
            // recreate this Activity when theme or font size changes
            if (o instanceof ThemeChangeEvent || o instanceof FontSizeChangeEvent) {
                getWindow().setWindowAnimations(R.style.Animation_Recreate);
                recreate();
            }
        });
    }

    @Override
    @CallSuper
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpToolbar();
        setupCoordinatorLayoutAnchorDelegate();
    }

    @Override
    @CallSuper
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setUpToolbar();
        setupCoordinatorLayoutAnchorDelegate();
    }

    @Override
    @CallSuper
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupDrawer();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @CallSuper
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerLayoutDelegate != null) {
            mDrawerLayoutDelegate.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MESSAGE_IF_SUCCESS) {
            if (resultCode == Activity.RESULT_OK) {
                showLongText(data.getStringExtra(EXTRA_MESSAGE));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpToolbar() {
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
                (CoordinatorLayout) findViewById(R.id.coordinator_layout));
    }

    @Override
    public final void setupFloatingActionButton(@DrawableRes int resId, View.OnClickListener onClickListener) {
        mCoordinatorLayoutAnchorDelegate.setupFloatingActionButton(resId, onClickListener);
    }

    /**
     * @see CoordinatorLayoutAnchorDelegate#showLongText(CharSequence)
     */
    @Override
    public final void showLongText(CharSequence text) {
        mCoordinatorLayoutAnchorDelegate.showLongText(text);
    }

    /**
     * @see CoordinatorLayoutAnchorDelegate#showLongSnackbarIfVisible(CharSequence, int, View.OnClickListener)
     */
    @Override
    public final Optional<Snackbar> showLongSnackbarIfVisible(CharSequence text, @StringRes int actionResId, View.OnClickListener onClickListener) {
        return mCoordinatorLayoutAnchorDelegate.showLongSnackbarIfVisible(text, actionResId,
                onClickListener);
    }

    /**
     * @see CoordinatorLayoutAnchorDelegate#showShortSnackbar(int)
     */
    @Override
    public final void showShortSnackbar(@StringRes int resId) {
        mCoordinatorLayoutAnchorDelegate.showShortSnackbar(resId);
    }
}
