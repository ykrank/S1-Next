package cl.monsoon.s1next.view.internal;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import cl.monsoon.s1next.R;

/**
 * THis class represents a delegate which you can use to add
 * {@link DrawerLayout} to {@link FragmentActivity}.
 */
public abstract class DrawerLayoutDelegate {

    final FragmentActivity mFragmentActivity;

    private final DrawerLayout mDrawerLayout;
    private final NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    private boolean mDrawerIndicatorEnabled = true;

    DrawerLayoutDelegate(FragmentActivity fragmentActivity, DrawerLayout drawerLayout, NavigationView navigationView) {
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
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /**
             * @see DrawerLayoutDelegate#closeDrawer(Runnable)
             */
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                Object tag = drawerView.getTag(R.id.tag_drawer_runnable);
                if (tag != null) {
                    Runnable runnable = (Runnable) tag;
                    drawerView.setTag(R.id.tag_drawer_runnable, null);
                    runnable.run();
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(mDrawerIndicatorEnabled);

        setupNavDrawerItem(mDrawerLayout, mNavigationView);
    }

    /**
     * Close the drawer view.
     *
     * @param runnable Execute this during the {@link ActionBarDrawerToggle#onDrawerClosed(View)}.
     */
    final void closeDrawer(@Nullable Runnable runnable) {
        mNavigationView.setTag(R.id.tag_drawer_runnable, runnable);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    protected abstract void setupNavDrawerItem(DrawerLayout drawerLayout, NavigationView navigationView);
}
