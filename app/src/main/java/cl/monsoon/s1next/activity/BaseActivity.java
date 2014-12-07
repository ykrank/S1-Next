package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.MyOkHttpClient;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.widget.MyRecyclerView;

/**
 * A base Activity which includes the toolbar, navigation drawer, login.
 * Also change theme depends on settings.
 */
public abstract class BaseActivity extends ActionBarActivity implements User.OnLogoutListener {

    public static final String ACTION_CHANGE_THEME = "change_theme";

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private ActionMenuView mActionMenuView;

    private boolean mIsToolbarShown = true;

    /**
     * We need to set up overlay Toolbar if we want to enable Toolbar's auto show/hide effect.
     * So we could start hiding Toolbar if main content has been full covered by Toolbar.
     * That's why this value always equals to Toolbar's height.
     */
    private int mToolbarAutoHideMinY;

    private DrawerLayout mDrawerLayout;
    private View mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mHasNavDrawer = true;
    private boolean mHasNavDrawerIndicator = true;

    /**
     * The user view is the area at the top of drawer.
     */
    private View mDrawerUserView;
    private TextView usernameView;
    private ImageView userAvatarView;

    private BroadcastReceiver themeChangeReceiver;
    private BroadcastReceiver userLoginStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme is DarkTheme
        if (Config.getCurrentTheme() == Config.LIGHT_THEME) {
            setTheme(Config.LIGHT_THEME);
        }

        super.onCreate(savedInstanceState);

        // change theme when night mode setting changes
        themeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                recreate();
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(themeChangeReceiver, new IntentFilter(ACTION_CHANGE_THEME));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(User.ACTION_USER_LOGIN);
        intentFilter.addAction(User.ACTION_USER_LOGOUT_OR_EXPIRATION);
        // change drawer's top area depends on user's login status
        userLoginStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(User.ACTION_USER_LOGIN)) {
                    setupDrawerUserView();
                } else {
                    setupDrawerLoginPrompt();
                }

                setupOthersWhenUserLoginStatusChanged(intent);
            }
        };
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(userLoginStatusReceiver, intentFilter);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userLoginStatusReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mHasNavDrawer) {
            setupNavDrawer();

            // Sync the toggle state after onRestoreInstanceState has occurred.
            if (mDrawerToggle != null) {
                mDrawerToggle.syncState();
            }

            if (isNavDrawerOpened()) {
                setupGlobalToolbar();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setUpToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                // designate a ToolBar as the ActionBar
                setSupportActionBar(mToolbar);
            }
        }
    }

    public void enableToolbarAutoHideEffect(MyRecyclerView myRecyclerView, RecyclerView.OnScrollListener onScrollListener) {
        mToolbarAutoHideMinY = ResourceUtil.getToolbarHeight(this);

        myRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (onScrollListener != null) {
                    onScrollListener.onScrolled(recyclerView, dx, dy);
                }

                // myRecyclerView.computeVerticalScrollOffset() may cause poor performance
                // so we also check mIsToolbarShown though we will do it later (over showOrHideToolbar(boolean))
                if (mIsToolbarShown
                        && dy > 0
                        && myRecyclerView.computeVerticalScrollOffset() >= mToolbarAutoHideMinY) {
                    showOrHideToolbar(false);
                } else if (dy < 0) {
                    showOrHideToolbar(true);
                }
            }
        });
    }

    void showOrHideToolbar(boolean show) {
        if (show == mIsToolbarShown) {
            return;
        }

        mIsToolbarShown = show;
        onToolbarAutoShowOrHide(show);
    }

    private void onToolbarAutoShowOrHide(boolean show) {
        if (show) {
            mToolbar.animate()
                    .alpha(1)
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator());
        } else {
            mToolbar.animate()
                    .alpha(0)
                    .translationY(-mToolbar.getBottom())
                    .setInterpolator(new DecelerateInterpolator());
        }
    }

    /**
     * Set ToolBar's navigation icon to cross.
     */
    void setupNavCrossIcon() {
        if (mToolbar != null) {

            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.menuCross, typedValue, true);
            mToolbar.setNavigationIcon(typedValue.resourceId);
        }
    }

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        mDrawer = mDrawerLayout.findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            /**
             * Only show items in the ToolBar relevant to this screen
             * if the drawer is not showing. Otherwise, let the drawer
             * decide what to show in the Toolbar.
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                setupGlobalToolbar();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                restoreToolbar();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (!mHasNavDrawerIndicator) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }

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

        setupNavDrawerItem();
    }

    private void setupNavDrawerItem() {
        if (mDrawerLayout == null || mDrawer == null) {
            return;
        }

        mDrawerUserView = mDrawer.findViewById(R.id.drawer_user_view);
        if (mDrawerUserView != null) {
            usernameView = (TextView) mDrawerUserView.findViewById(R.id.drawer_username);
            userAvatarView = (ImageView) mDrawerUserView.findViewById(R.id.drawer_user_avatar);

            // Show default avatar and login prompt if user hasn't logged in,
            // else show user's avatar and username.
            if (TextUtils.isEmpty(User.getName())) {
                setupDrawerLoginPrompt();
            } else {
                setupDrawerUserView();
            }
        }

        // add settings item
        TextView settingsView = (TextView) mDrawer.findViewById(R.id.settings);
        settingsView.setText(getText(R.string.settings));

        // set up settings icon
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.iconSettings, typedValue, true);
        settingsView.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(R.dimen.left_icon_margin_right));
        settingsView.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(typedValue.resourceId), null, null, null);

        // start SettingsActivity if clicked
        settingsView.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mDrawer);

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Show default avatar and login prompt if user hasn't logged in.
     */
    private void setupDrawerLoginPrompt() {
        if (mDrawerLayout == null || mDrawer == null && usernameView != null) {
            return;
        }

        // setup default avatar
        Glide.with(this)
                .load(R.drawable.ic_avatar_placeholder)
                .transform(new CenterCrop(Glide.get(this).getBitmapPool()))
                .into(userAvatarView);

        // start LoginActivity if clicked
        usernameView.setText(R.string.action_login);
        mDrawerUserView.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mDrawer);

            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Show user's avatar and username if user has logged in.
     */
    void setupDrawerUserView() {
        if (mDrawerLayout == null || mDrawer == null && usernameView != null) {
            return;
        }

        // setup user's avatar
        Glide.with(this)
                .load(Api.getUrlAvatarMedium(User.getUid()))
                .error(R.drawable.ic_avatar_placeholder)
                .transform(new CenterCrop(Glide.get(this).getBitmapPool()))
                .into(userAvatarView);

        // show logout dialog if clicked
        usernameView.setText(User.getName());
        mDrawer.findViewById(R.id.drawer_user_view).setOnClickListener(v ->
                new LogoutDialog().show(getFragmentManager(), LogoutDialog.TAG));
    }

    void setupOthersWhenUserLoginStatusChanged(Intent intent) {

    }

    /**
     * Per the navigation drawer design guidelines, updates the ToolBar to show the global app
     * context when drawer opened, rather than just what's in the current screen.
     */
    void setupGlobalToolbar() {
        if (mToolbar == null) {
            return;
        }

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
        if (mToolbar == null) {
            return;
        }

        setTitle(mTitle);

        // show menu in Toolbar
        if (mActionMenuView != null) {
            mActionMenuView.setVisibility(View.VISIBLE);
        }
    }

    Toolbar getToolbar() {
        return mToolbar;
    }

    void setNavDrawerEnabled(boolean enabled) {
        mHasNavDrawer = enabled;
    }

    void setNavDrawerIndicatorEnabled(boolean enabled) {
        mHasNavDrawerIndicator = enabled;
    }

    private boolean isNavDrawerOpened() {
        return mDrawerLayout != null && mDrawer != null && mDrawerLayout.isDrawerOpen(mDrawer);
    }

    public static class LogoutDialog extends DialogFragment {

        private static final String TAG = "log_out_dialog";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_progress_log_out)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> {
                                        if (getActivity() instanceof User.OnLogoutListener) {
                                            ((User.OnLogoutListener) getActivity()).onLogout();
                                        } else {
                                            throw new ClassCastException(
                                                    getActivity()
                                                            + " must implements  User.OnLogoutListener.");
                                        }
                                    })
                            .setNegativeButton(
                                    android.R.string.cancel, null)
                            .create();
        }
    }

    @Override
    public void onLogout() {
        // clear cookie and current user's info
        MyOkHttpClient.clearCookie();
        User.clear();
        User.sendLogoutOrExpirationBroadcast();
    }
}
