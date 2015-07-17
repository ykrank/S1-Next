package cl.monsoon.s1next.view.internal;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import cl.monsoon.s1next.R;

/**
 * A helper class warps drawer.
 */
public abstract class DrawerLayoutPresenter {

    /**
     * Same to {@link android.support.v4.widget.ViewDragHelper#BASE_SETTLE_DURATION}.
     */
    private static final int DRAWER_SETTLE_DURATION = 256;

    final FragmentActivity mFragmentActivity;

    private final DrawerLayout mDrawerLayout;
    private final NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    private boolean mDrawerIndicatorEnabled = true;

    DrawerLayoutPresenter(FragmentActivity fragmentActivity, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.mFragmentActivity = fragmentActivity;
        this.mDrawerLayout = drawerLayout;
        this.mNavigationView = navigationView;
    }

    /**
     * @see android.support.v7.app.AppCompatActivity#onPostCreate(Bundle)
     */
    @CallSuper
    public void onPostCreate() {
        setupNavDrawer();

        // Syncs the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * @see android.support.v7.app.AppCompatActivity#onOptionsItemSelected(MenuItem)
     */
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event.
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    /**
     * @see android.support.v7.app.AppCompatActivity#onConfigurationChanged(Configuration)
     */
    @CallSuper
    public void onConfigurationChanged(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Enable or disable the drawer indicator. The indicator defaults to enabled.
     */
    public final void setDrawerIndicatorEnabled(boolean enabled) {
        mDrawerIndicatorEnabled = enabled;
    }

    private void setupNavDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(mFragmentActivity, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(mDrawerIndicatorEnabled);

        setupNavDrawerItem(mDrawerLayout, mNavigationView);
    }

    /**
     * Close the drawer view.
     *
     * @param runnable execute this after drawer view closing.
     */
    final void closeDrawer(Runnable runnable) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(runnable::run, DRAWER_SETTLE_DURATION);
    }

    protected abstract void setupNavDrawerItem(DrawerLayout drawerLayout, NavigationView navigationView);
}
