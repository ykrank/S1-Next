package cl.monsoon.s1next.view.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.event.FontSizeChangeEvent;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.view.internal.DrawerLayoutPresenter;
import cl.monsoon.s1next.view.internal.DrawerLayoutPresenterConcrete;
import cl.monsoon.s1next.view.internal.ToolbarPresenter;

/**
 * A base Activity which includes the Toolbar
 * and navigation drawer amongst others.
 * Also changes theme depends on settings.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    @Inject
    User mUser;

    private ToolbarPresenter mToolbarPresenter;

    private DrawerLayoutPresenter mDrawerLayoutPresenter;
    private boolean mDrawerIndicatorEnabled = true;

    private Object mEvents;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent(this).inject(this);
        // change the theme depends on preference
        if (!mThemeManager.isDefaultTheme()) {
            setTheme(mThemeManager.getThemeStyle());
        }

        super.onCreate(savedInstanceState);

        // https://github.com/square/otto/issues/26
        mEvents = new Object() {

            /**
             * Recreate this Activity when theme changes.
             */
            @Subscribe
            @SuppressWarnings("unused")
            public void changeTheme(ThemeChangeEvent event) {
                recreate();
            }

            /**
             * Recreate this Activity when font size changes.
             */
            @Subscribe
            @SuppressWarnings("unused")
            public void changeFontSize(FontSizeChangeEvent event) {
                recreate();
            }
        };

        BusProvider.get().register(mEvents);
    }

    @Override
    @CallSuper
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpToolbar();
    }

    @Override
    @CallSuper
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setUpToolbar();
    }

    @Override
    @CallSuper
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupDrawer();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();

        BusProvider.get().unregister(mEvents);
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event.
        if (mDrawerLayoutPresenter != null && mDrawerLayoutPresenter.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                // according to https://developer.android.com/design/patterns/navigation.html
                // we should navigate to its hierarchical parent of the current screen
                // but the hierarchical logical is too complex in our app (sub forum, link redirection)
                // and sometimes really confuses people
                // so we use finish() to close the current Activity
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

        if (mDrawerLayoutPresenter != null) {
            mDrawerLayoutPresenter.onConfigurationChanged(newConfig);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            mToolbarPresenter = new ToolbarPresenter(this, toolbar);
        }
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    final void setupNavCrossIcon() {
        mToolbarPresenter.setupNavCrossIcon();
    }

    @Nullable
    final Toolbar getToolbar() {
        return mToolbarPresenter.getToolbar();
    }

    private void setupDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            mDrawerLayoutPresenter = new DrawerLayoutPresenterConcrete(this, drawerLayout,
                    (NavigationView) findViewById(R.id.navigation_view));
            mDrawerLayoutPresenter.setDrawerIndicatorEnabled(mDrawerIndicatorEnabled);
            mDrawerLayoutPresenter.onPostCreate();
        }
    }

    /**
     * Call this method before {@link #onPostCreate(Bundle)}
     * otherwise it doesn't works.
     */
    final void setDrawerIndicatorEnabled(boolean enabled) {
        mDrawerIndicatorEnabled = enabled;
    }

    /**
     * Subclass must have to implement {@link android.view.View.OnClickListener}
     * in order to use this method.
     */
    final void setupFloatingActionButton(@DrawableRes int resId) {
        ViewGroup container = (ViewGroup) findViewById(R.id.coordinator_layout);
        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getLayoutInflater().inflate(R.layout.floating_action_button, container, false);
        container.addView(floatingActionButton);

        floatingActionButton.setOnClickListener((View.OnClickListener) this);
        floatingActionButton.setImageResource(resId);
    }
}
