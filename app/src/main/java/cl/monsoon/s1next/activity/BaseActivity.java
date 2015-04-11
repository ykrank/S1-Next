package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.event.FontSizeChangeEvent;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.event.UserStatusEvent;
import cl.monsoon.s1next.fragment.BaseFragment;
import cl.monsoon.s1next.fragment.SettingsFragment;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.singleton.OkHttpClientProvider;
import cl.monsoon.s1next.singleton.Settings;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.BaseRecyclerView;
import cl.monsoon.s1next.view.InsetsFrameLayout;

/**
 * A base Activity which includes the Toolbar tweaks
 * and navigation drawer amongst others.
 * Also changes theme depends on settings.
 */
public abstract class BaseActivity extends ActionBarActivityCompat
        implements InsetsFrameLayout.OnInsetsCallback, BaseFragment.InsetsCallback {

    private Rect mSystemWindowInsets;
    private final List<InsetsFrameLayout.OnInsetsCallback> onInsetsCallbackList =
            Collections.synchronizedList(new ArrayList<>());

    private Toolbar mToolbar;
    private boolean mIsToolbarShown = true;

    /**
     * We need to set up overlay Toolbar if we want to enable Toolbar's auto show/hide effect.
     * We could start hiding Toolbar if main content has been full covered by Toolbar,
     * that's why this value always equals to Toolbar's height.
     */
    private int mToolbarAutoHideMinY;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private DrawerLayout mDrawerLayout;
    private View mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mHasNavDrawer = true;
    private boolean mHasNavDrawerIndicator = true;

    private View mDrawerTopBackgroundView;
    private ImageView mDrawerUserAvatarView;
    private TextView mDrawerUsernameView;

    private FloatingActionButton mFloatingActionButton;

    private Object mEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.Theme.isDefaultTheme()) {
            setTheme(Settings.Theme.getCurrentTheme());
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
                if (!(BaseActivity.this instanceof SettingsActivity)) {
                    recreate();
                }
            }

            /**
             * Change drawer's top area depends on user's login status.
             */
            @Subscribe
            @SuppressWarnings("unused")
            public void updateDrawer(UserStatusEvent event) {
                if (event.getUserStatus() == UserStatusEvent.USER_LOGIN) {
                    setupDrawerUserView();
                } else {
                    setupDrawerLoginPrompt();
                }
            }
        };

        BusProvider.get().register(mEvents);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpToolbar();

        InsetsFrameLayout insetsFrameLayout =
                (InsetsFrameLayout) findViewById(R.id.insets_frame_layout);
        if (insetsFrameLayout != null) {
            insetsFrameLayout.setOnInsetsCallback(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BusProvider.get().unregister(mEvents);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event.
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mHasNavDrawer) {
            setupNavDrawer();

            // Syncs the toggle state after onRestoreInstanceState has occurred.
            if (mDrawerToggle != null) {
                mDrawerToggle.syncState();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    void enableWindowTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setUpToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                // designate a Toolbar as the ActionBar
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /**
     * This method only useful if API >= 19 (which we can enable translucent status bar).
     * But we don't use `fitsSystemWindows` to adjust layout
     * in order to show overlay status bar & Toolbar.
     */
    @Override
    public void onInsetsChanged(Rect insets) {
        mSystemWindowInsets = insets;

        int insetsTop = insets.top;

        if (mToolbar != null) {
            // We need to set Toolbar's padding
            // instead of translucent/transparent status bar.
            mToolbar.setPadding(0, insetsTop, 0, 0);
            mToolbar.getLayoutParams().height = insetsTop + ResourceUtil.getToolbarHeight();
            mToolbar.requestLayout();
        }

        if (mDrawerTopBackgroundView != null && mDrawerUserAvatarView != null) {
            // adjust drawer top background & user avatar's layout
            mDrawerTopBackgroundView.getLayoutParams().height = insetsTop
                    + getResources().getDimensionPixelSize(R.dimen.drawer_top_height);
            mDrawerTopBackgroundView.requestLayout();

            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) mDrawerUserAvatarView.getLayoutParams();
            marginLayoutParams.topMargin = insetsTop
                    + getResources().getDimensionPixelSize(R.dimen.drawer_avatar_margin_top);
            mDrawerUserAvatarView.requestLayout();
        }

        for (InsetsFrameLayout.OnInsetsCallback onInsetsCallback : onInsetsCallbackList) {
            onInsetsCallback.onInsetsChanged(insets);
        }
    }

    /**
     * Implements {@link BaseFragment.InsetsCallback}.
     */
    @Override
    public void register(InsetsFrameLayout.OnInsetsCallback onInsetsCallback) {
        onInsetsCallbackList.add(onInsetsCallback);
    }

    @Override
    public void unregister(InsetsFrameLayout.OnInsetsCallback onInsetsCallback) {
        onInsetsCallbackList.remove(onInsetsCallback);
    }

    @Override
    public Rect getSystemWindowInsets() {
        if (mSystemWindowInsets == null) {
            mSystemWindowInsets = new Rect();
        }

        return mSystemWindowInsets;
    }

    void setupFloatingActionButton(@DrawableRes int resId) {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mFloatingActionButton.setShadow(false);
        }
        // subclass need to implement android.view.View.OnClickListener
        mFloatingActionButton.setOnClickListener((View.OnClickListener) this);
        mFloatingActionButton.setImageResource(resId);
        mFloatingActionButton.setVisibility(View.VISIBLE);
    }

    /**
     * Also enables {@link cl.monsoon.s1next.activity.BaseActivity#mFloatingActionButton}
     * auto show/hide effect.
     */
    public void enableToolbarAndFabAutoHideEffect(BaseRecyclerView baseRecyclerView, @Nullable RecyclerView.OnScrollListener onScrollListener) {
        mToolbarAutoHideMinY = ResourceUtil.getToolbarHeight();

        baseRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (onScrollListener != null) {
                    onScrollListener.onScrolled(recyclerView, dx, dy);
                }

                // baseRecyclerView.computeVerticalScrollOffset() may cause poor performance
                // so we also check mIsToolbarShown though we will do it later (during showOrHideToolbarAndFab(boolean))
                if (mIsToolbarShown
                        && dy > 0
                        && baseRecyclerView.computeVerticalScrollOffset() >= mToolbarAutoHideMinY) {
                    showOrHideToolbarAndFab(false);
                } else if (dy < 0) {
                    showOrHideToolbarAndFab(true);
                }
            }
        });
    }

    public void showOrHideToolbarAndFab(boolean show) {
        if (show == mIsToolbarShown) {
            return;
        }

        mIsToolbarShown = show;
        onToolbarAutoShowOrHide(show);
        onFloatingActionButtonAutoShowOrHide(show);
    }

    private void onToolbarAutoShowOrHide(boolean show) {
        if (show) {
            mToolbar.animate()
                    .alpha(1)
                    .translationY(0)
                    .setInterpolator(mInterpolator);
        } else {
            mToolbar.animate()
                    .alpha(0)
                    .translationY(-mToolbar.getBottom())
                    .setInterpolator(mInterpolator);
        }
    }

    private void onFloatingActionButtonAutoShowOrHide(boolean show) {
        if (mFloatingActionButton != null) {
            if (show) {
                mFloatingActionButton.show();
            } else {
                mFloatingActionButton.hide();
            }
        }
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    void setupNavCrossIcon() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(ResourceUtil.getResourceId(getTheme(), R.attr.menuCross));
        }
    }

    void setNavDrawerEnabled(boolean enabled) {
        mHasNavDrawer = enabled;
    }

    void setNavDrawerIndicatorEnabled(boolean enabled) {
        mHasNavDrawerIndicator = enabled;
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
             * Only show items in the Toolbar relevant to this screen
             * if the drawer is not showing. Otherwise, let the drawer
             * decide what to show in the Toolbar.
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                showOrHideToolbarAndFab(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (drawerView.getTag() instanceof Runnable) {
                    Runnable runnable = (Runnable) drawerView.getTag();
                    drawerView.setTag(null);
                    runnable.run();
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (!mHasNavDrawerIndicator) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }

        // According to the Google Material Design,
        // width = screen width - app bar height in mobile.
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        ViewGroup.LayoutParams layoutParams = mDrawer.getLayoutParams();
        layoutParams.width = point.x - getResources().getDimensionPixelSize(
                R.dimen.abc_action_bar_default_height_material);
        mDrawer.requestLayout();

        setupNavDrawerItem();
    }

    private void closeDrawer(@Nullable Runnable runnable) {
        if (mDrawerLayout != null && mDrawer != null) {
            mDrawerLayout.post(() -> {
                mDrawer.setTag(runnable);
                mDrawerLayout.closeDrawer(mDrawer);
            });
        }
    }

    private void setupNavDrawerItem() {
        if (mDrawerLayout == null || mDrawer == null) {
            return;
        }

        mDrawerTopBackgroundView = mDrawer.findViewById(R.id.drawer_top_background);
        mDrawerUserAvatarView = (ImageView) mDrawer.findViewById(R.id.drawer_user_avatar);
        mDrawerUserAvatarView.setOnClickListener(v ->
                new ThemeChangeDialog().show(getSupportFragmentManager(), ThemeChangeDialog.TAG));
        mDrawerUsernameView = (TextView) mDrawer.findViewById(R.id.drawer_username);

        // Show default avatar and login prompt if user hasn't logged in,
        // else show user's avatar and username.
        if (User.hasLoggedIn()) {
            setupDrawerUserView();
        } else {
            setupDrawerLoginPrompt();
        }

        // add home item
        TextView homeView = (TextView) mDrawer.findViewById(R.id.home);
        homeView.setText(R.string.home);

        final int margin = getResources().getDimensionPixelSize(R.dimen.left_icon_margin_right);
        homeView.setCompoundDrawablePadding(margin);
        homeView.setCompoundDrawablesWithIntrinsicBounds(
                ResourceUtil.getResourceId(getTheme(), R.attr.iconHome), 0, 0, 0);
        // back to forum (home) activity if clicked
        homeView.setOnClickListener(v ->
                closeDrawer(() -> {
                    if (this instanceof ForumActivity) {
                        return;
                    }

                    Intent intent = new Intent(this, ForumActivity.class);
                    // FLAG_ACTIVITY_NEW_TASK only works if this Activity is launched from other apps
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    // this is hard to explain why we use finishAffinity() here
                    //
                    // Precondition: we set a unique taskAffinity for ForumActivity in AndroidManifest.xml
                    //
                    // Scenario 1 (our app): -> start other Activities several times -> Home (by drawer)
                    //                       -> finish all Activities exclude ForumActivity
                    //                          (because ForumActivity has a unique taskAffinity)
                    //
                    // Scenario 2(other apps): -> PostListActivity (by Intent filter)
                    //                         -> start other Activities several times -> Home (by drawer)
                    //                         -> finish all our Activities in this app
                    //                         -> new task in our app
                    ActivityCompat.finishAffinity(this);
                }));

        // add settings item
        TextView settingsView = (TextView) mDrawer.findViewById(R.id.settings);
        settingsView.setText(getText(R.string.settings));
        settingsView.setCompoundDrawablePadding(margin);
        settingsView.setCompoundDrawablesWithIntrinsicBounds(
                ResourceUtil.getResourceId(getTheme(), R.attr.iconSettings), 0, 0, 0);
        // start SettingsActivity if clicked
        settingsView.setOnClickListener(v ->
                closeDrawer(() -> {
                    Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }));
    }

    /**
     * Show default avatar and login prompt if user hasn't logged in.
     */
    private void setupDrawerLoginPrompt() {
        if (mDrawerLayout == null || mDrawer == null
                || mDrawerTopBackgroundView == null
                || mDrawerUserAvatarView == null || mDrawerUsernameView == null) {
            return;
        }

        // setup default avatar
        Glide.with(this)
                .load(R.drawable.ic_drawer_avatar_placeholder)
                .signature(Settings.Download.getAvatarCacheInvalidationIntervalSignature())
                .transform(new CenterCrop(Glide.get(this).getBitmapPool()))
                .into(mDrawerUserAvatarView);

        // start LoginActivity if clicked
        mDrawerUsernameView.setText(R.string.action_login);

        mDrawerTopBackgroundView.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mDrawer);

            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Show user's avatar and username if user has logged in.
     */
    private void setupDrawerUserView() {
        if (mDrawerTopBackgroundView == null
                || mDrawerUserAvatarView == null || mDrawerUsernameView == null) {
            return;
        }

        // setup user's avatar
        Glide.with(this)
                .load(Api.getAvatarMediumUrl(User.getUid()))
                .signature(Settings.Download.getAvatarCacheInvalidationIntervalSignature())
                .error(R.drawable.ic_drawer_avatar_placeholder)
                .transform(new CenterCrop(Glide.get(this).getBitmapPool()))
                .into(mDrawerUserAvatarView);

        // show logout dialog if clicked
        mDrawerUsernameView.setText(User.getName());

        mDrawerTopBackgroundView.setOnClickListener(v ->
                new LogoutDialog().show(getSupportFragmentManager(), LogoutDialog.TAG));
    }

    Toolbar getToolbar() {
        return mToolbar;
    }

    public static class ThemeChangeDialog extends DialogFragment {

        private static final String TAG = ThemeChangeDialog.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());

            //noinspection ConstantConditions
            int checkedItem = Integer.parseInt(sharedPreferences.getString(
                    SettingsFragment.PREF_KEY_THEME,
                    getString(R.string.pref_theme_default_value)));
            return
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.pref_theme)
                            .setSingleChoiceItems(
                                    R.array.pref_theme_entries,
                                    checkedItem,
                                    (dialog, which) -> {
                                        // won't change theme if unchanged
                                        if (which != checkedItem) {
                                            sharedPreferences.edit()
                                                    .putString(SettingsFragment.PREF_KEY_THEME,
                                                            String.valueOf(which)).apply();
                                            Settings.Theme.setCurrentTheme(sharedPreferences);

                                            BusProvider.get().post(new ThemeChangeEvent());
                                        }
                                        dismiss();
                                        ((BaseActivity) getActivity()).closeDrawer(null);
                                    })
                            .create();
        }
    }

    public static class LogoutDialog extends DialogFragment {

        private static final String TAG = LogoutDialog.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.dialog_message_log_out)
                    .setPositiveButton(
                            android.R.string.ok,
                            (dialog, which) -> ((BaseActivity) getActivity()).onLogout())
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }

    private void onLogout() {
        // clear account cookie and current user's info
        OkHttpClientProvider.clearCookie();
        User.reset();
        User.sendCookieExpirationEvent();
    }
}
