package cl.monsoon.s1next.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.widget.RoundedBitmapTransformation;

/**
 * An abstract Activity to create a navigation drawer.
 * Must not call setContentView in subclass's {@link #onCreate(android.os.Bundle)}.
 */
public abstract class AbsNavigationDrawerActivity extends AbsThemeActivity {

    Toolbar mToolbar;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    private View mDrawer;
    private ActionMenuView mActionMenuView;
    private CharSequence mTitle;

    private boolean showLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // designate a ToolBar as the ActionBar
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawer = findViewById(R.id.drawer);
        Glide.with(this)
                .load(R.drawable.ic_avatar_placeholder)
                .transform(new RoundedBitmapTransformation(this))
                .into((ImageView) mDrawer.findViewById(R.id.drawer_avatar));
        mDrawer.findViewById(R.id.drawer_header).setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mDrawer);

            Intent intent = new Intent(AbsNavigationDrawerActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        TextView textView = (TextView) mDrawer.findViewById(R.id.settings);
        textView.setText(getText(R.string.settings));
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.iconSettings, typedValue, true);
        textView.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(R.dimen.left_icon_margin_right));
        textView.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(typedValue.resourceId), null, null, null);

        textView.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mDrawer);

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            /**
             * Only show items in the ToolBar relevant to this screen
             * if the drawer is not showing. Otherwise, let the drawer
             * decide what to show in the Toolbar.
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                showGlobalContext();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                restoreToolbar();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // According to the Google Material Design,
        // Mobile: Width = screen width - app bar height.
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        ViewGroup.LayoutParams layoutParams = mDrawer.getLayoutParams();
        layoutParams.width =
                point.x -
                        getResources().getDimensionPixelSize(
                                R.dimen.abc_action_bar_default_height_material);
        mDrawer.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showUserInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            showGlobalContext();
        }
    }

    /**
     * Per the navigation drawer design guidelines, updates the ToolBar to show the global app
     * context when drawer opened, rather than just what's in the current screen.
     */
    void showGlobalContext() {
        mTitle = getTitle();
        setTitle(R.string.app_name);

        // Hide menu in Toolbar.
        // Because mToolbar.getChildAt() doesn't return
        // right view at the specified position,
        // so use loop to get the ActionMenuView.
        if (mActionMenuView == null) {
            int count = mToolbar.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = mToolbar.getChildAt(i);
                if (view instanceof ActionMenuView) {
                    mActionMenuView = (ActionMenuView) view;
                    mActionMenuView.setVisibility(View.GONE);
                    break;
                }
            }
        } else {
            mActionMenuView.setVisibility(View.GONE);
        }
    }

    /**
     * Restore ToolBar when drawer closed.
     * <p>
     * Subclass must call {@code super.restoreToolbar()}.
     */
    void restoreToolbar() {
        setTitle(mTitle);

        // show menu in Toolbar
        if (mActionMenuView != null) {
            mActionMenuView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show username and its avatar when user logged in.
     */
    public void showUserInfo() {
        if (!TextUtils.isEmpty(Config.getUsername()) && !showLogin) {
            showLogin = true;

            ((TextView) mDrawer.findViewById(R.id.drawer_username)).setText(Config.getUsername());
            mDrawer.findViewById(R.id.drawer_header).setOnClickListener(null);

            Glide.with(this)
                    .load(Api.getUrlAvatarMedium(Config.getUid()))
                    .error(R.drawable.ic_avatar_placeholder)
                    .transform(new RoundedBitmapTransformation(this))
                    .into((ImageView) mDrawer.findViewById(R.id.drawer_avatar));
        }
    }
}
