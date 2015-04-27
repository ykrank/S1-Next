package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationDrawerView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.event.FontSizeChangeEvent;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.event.UserStatusEvent;
import cl.monsoon.s1next.fragment.BaseFragment;
import cl.monsoon.s1next.fragment.MainPreferenceFragment;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.singleton.OkHttpClientProvider;
import cl.monsoon.s1next.singleton.Settings;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.InsetsFrameLayout;

/**
 * A base Activity which includes the Toolbar tweaks
 * and navigation drawer amongst others.
 * Also changes theme depends on settings.
 */
public abstract class BaseActivity extends AppCompatActivityCompat
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
    private int mFabMarginBottom;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mHasNavDrawerIndicator = true;

    private View mDrawerHeaderBackgroundView;
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
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    runOnUiThread(() -> {
                        if (event.getUserStatus() == UserStatusEvent.USER_LOGIN) {
                            setupDrawerUserView();
                        } else {
                            setupDrawerLoginPrompt();
                        }
                    });
                }
            }
        };

        BusProvider.get().register(mEvents);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpToolbar();

        InsetsFrameLayout insetsFrameLayout = (InsetsFrameLayout) findViewById(
                R.id.insets_frame_layout);
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

        setupNavDrawer();

        // Syncs the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
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
                //noinspection ConstantConditions
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
    public void onInsetsChanged(@NonNull Rect insets) {
        mSystemWindowInsets = insets;

        int insetsTop = insets.top;

        if (mToolbar != null) {
            // We need to set Toolbar's padding
            // instead of translucent/transparent status bar.
            mToolbar.setPadding(0, insetsTop, 0, 0);
            mToolbar.getLayoutParams().height = insetsTop + ResourceUtil.getToolbarHeight();
            mToolbar.requestLayout();
        }

        if (mDrawerHeaderBackgroundView != null && mDrawerUserAvatarView != null) {
            // adjust drawer top background & user avatar's layout
            mDrawerHeaderBackgroundView.getLayoutParams().height = insetsTop
                    + getResources().getDimensionPixelSize(R.dimen.drawer_top_height);
            mDrawerHeaderBackgroundView.requestLayout();

            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                    mDrawerUserAvatarView.getLayoutParams();
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

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    void setupNavCrossIcon() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(ResourceUtil.getResourceId(getTheme(), R.attr.menuCross));
        }
    }

    void setNavDrawerIndicatorEnabled(boolean enabled) {
        mHasNavDrawerIndicator = enabled;
    }

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        mDrawer = (NavigationDrawerView) mDrawerLayout.findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

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
        if (mDrawer == null) {
            return;
        }

        View drawerHeaderView = mDrawer.inflateHeaderView(R.layout.drawer_header);
        mDrawerHeaderBackgroundView = drawerHeaderView.findViewById(R.id.drawer_header_background);
        mDrawerUserAvatarView = (ImageView) drawerHeaderView.findViewById(R.id.drawer_user_avatar);
        mDrawerUserAvatarView.setOnClickListener(v ->
                new ThemeChangeDialog().show(getSupportFragmentManager(), ThemeChangeDialog.TAG));
        mDrawerUsernameView = (TextView) drawerHeaderView.findViewById(R.id.drawer_username);

        // Show default avatar and login prompt if user hasn't logged in,
        // else show user's avatar and username.
        if (User.hasLoggedIn()) {
            setupDrawerUserView();
        } else {
            setupDrawerLoginPrompt();
        }

        Menu drawerMenu = mDrawer.getMenu();
        getMenuInflater().inflate(R.menu.drawer, drawerMenu);
        mDrawer.setNavigationItemSelectedListener(item -> {
            Callable<Void> callable;
            switch (item.getItemId()) {
                case R.id.home:
                    callable = this::onHomeMenuSelected;

                    break;
                case R.id.favourites:
                    callable = this::onFavouritesMenuSelected;

                    break;
                case R.id.settings:
                    callable = this::onSettingsMenuSelected;

                    break;
                default:
                    callable = null;
            }

            if (callable != null) {
                closeDrawer(() -> {
                    try {
                        callable.call();
                    } catch (Exception ignored) {

                    }
                });

                return true;
            }

            return false;
        });
    }

    private Void onHomeMenuSelected() {
        if (!(this instanceof ForumActivity)) {
            Intent intent = new Intent(this, ForumActivity.class);
            if (NavUtils.shouldUpRecreateTask(this, intent)) {
                // finish all our Activities in this app
                ActivityCompat.finishAffinity(this);
                // this activity is not part of this app's task
                // so create a new task when navigating up with
                // a synthesized back stack
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(intent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, intent);
            }
        }

        return null;
    }

    private Void onFavouritesMenuSelected() {
        if (!(this instanceof FavouriteListActivity)) {
            if (checkUserLoggedInStatus()) {
                Intent intent = new Intent(this, FavouriteListActivity.class);
                startActivity(intent);
            }
        }

        return null;
    }

    private Void onSettingsMenuSelected() {
        Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
        startActivity(intent);

        return null;
    }

    /**
     * Show default avatar and login prompt if user hasn't logged in.
     */
    private void setupDrawerLoginPrompt() {
        if (mDrawerLayout == null || mDrawer == null
                || mDrawerHeaderBackgroundView == null
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

        mDrawerHeaderBackgroundView.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mDrawer);

            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Show user's avatar and username if user has logged in.
     */
    private void setupDrawerUserView() {
        if (mDrawerHeaderBackgroundView == null
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

        mDrawerHeaderBackgroundView.setOnClickListener(v ->
                new LogoutDialog().show(getSupportFragmentManager(), LogoutDialog.TAG));
    }

    void setupFloatingActionButton(@DrawableRes int resId) {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        // subclass need to implement android.view.View.OnClickListener
        mFloatingActionButton.setOnClickListener((View.OnClickListener) this);
        mFloatingActionButton.setImageResource(resId);
        mFloatingActionButton.setVisibility(View.VISIBLE);
    }

    public void enableToolbarAndFabAutoHideEffect(RecyclerView recyclerView) {
        mToolbarAutoHideMinY = ResourceUtil.getToolbarHeight();
        mFabMarginBottom = getResources().getDimensionPixelSize(R.dimen.fab_margin);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // recyclerView.computeVerticalScrollOffset() may cause poor performance
                // so we also check mIsToolbarShown though we will do it later (during showOrHideToolbarAndFab(boolean))
                if (mIsToolbarShown
                        && dy > 0
                        && recyclerView.computeVerticalScrollOffset() >= mToolbarAutoHideMinY) {
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
        onFabAutoShowOrHide(show);
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
                    .translationY(-mToolbar.getHeight())
                    .setInterpolator(mInterpolator);
        }
    }

    private void onFabAutoShowOrHide(boolean show) {
        if (mFloatingActionButton != null) {
            if (show) {
                mFloatingActionButton.animate()
                        .alpha(1)
                        .translationY(0)
                        .setInterpolator(mInterpolator);
            } else {
                mFloatingActionButton.animate()
                        .alpha(0)
                        .translationY(mFloatingActionButton.getHeight() + mFabMarginBottom)
                        .setInterpolator(mInterpolator);
            }
        }
    }

    Toolbar getToolbar() {
        return mToolbar;
    }

    public static class ThemeChangeDialog extends DialogFragment {

        private static final String TAG = ThemeChangeDialog.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    getActivity());

            //noinspection ConstantConditions
            int checkedItem = Integer.parseInt(sharedPreferences.getString(
                    MainPreferenceFragment.PREF_KEY_THEME,
                    getString(R.string.pref_theme_default_value)));
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pref_theme)
                    .setSingleChoiceItems(
                            R.array.pref_theme_entries,
                            checkedItem,
                            (dialog, which) -> {
                                // won't change theme if unchanged
                                if (which != checkedItem) {
                                    sharedPreferences.edit().putString(
                                            MainPreferenceFragment.PREF_KEY_THEME,
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

    public static class LoginPromptDialog extends DialogFragment {

        private static final String TAG = LoginPromptDialog.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.dialog_message_login_prompt)
                    .setPositiveButton(R.string.action_login,
                            (dialog, which) -> {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }

    /**
     * Show {@link LoginPromptDialog} if user hasn't logged in.
     *
     * @return whether user has logged in
     */
    boolean checkUserLoggedInStatus() {
        if (!User.hasLoggedIn()) {
            new LoginPromptDialog().show(getSupportFragmentManager(), LoginPromptDialog.TAG);

            return false;
        }

        return true;
    }

    public static class LogoutDialog extends DialogFragment {

        private static final String TAG = LogoutDialog.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.dialog_message_log_out)
                    .setPositiveButton(
                            R.string.dialog_button_text_log_out,
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
