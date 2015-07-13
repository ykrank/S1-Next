package cl.monsoon.s1next.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.squareup.otto.Subscribe;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.event.FontSizeChangeEvent;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.event.UserStatusEvent;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.singleton.OkHttpClientProvider;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ResourceUtil;

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

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mHasNavDrawerIndicator = true;

    private View mDrawerHeaderBackgroundView;
    private ImageView mDrawerUserAvatarView;
    private TextView mDrawerUsernameView;

    private Object mEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent(this).inject(this);
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

            /**
             * Change drawer's top area depends on user's login status.
             */
            @Subscribe
            @SuppressWarnings("unused")
            public void updateDrawer(UserStatusEvent event) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    checkUserStatus(event);
                } else {
                    runOnUiThread(() -> checkUserStatus(event));
                }
            }

            private void checkUserStatus(UserStatusEvent event) {
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

        mNavigationView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_view);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                Object tag = drawerView.getTag(R.id.drawer_runnable_tag);
                if (tag instanceof Runnable) {
                    Runnable runnable = (Runnable) tag;
                    drawerView.setTag(R.id.drawer_runnable_tag, null);
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
        if (mDrawerLayout != null && mNavigationView != null) {
            mDrawerLayout.post(() -> {
                mNavigationView.setTag(R.id.drawer_runnable_tag, runnable);
                mDrawerLayout.closeDrawer(mNavigationView);
            });
        }
    }

    private void setupNavDrawerItem() {
        if (mNavigationView == null) {
            return;
        }

        View drawerHeaderView = findViewById(R.id.drawer_header);
        mDrawerHeaderBackgroundView = findViewById(R.id.drawer_header_background);
        mDrawerUserAvatarView = (ImageView) drawerHeaderView.findViewById(R.id.drawer_user_avatar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // let DrawerLayout draw the insets area for the status bar
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            // add status bar height to drawer's header
            mDrawerLayout.setOnApplyWindowInsetsListener((v, insets) -> {

                int insetsTop = insets.getSystemWindowInsetTop();

                mDrawerHeaderBackgroundView.getLayoutParams().height = insetsTop
                        + getResources().getDimensionPixelSize(R.dimen.drawer_top_height);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                        mDrawerUserAvatarView.getLayoutParams();
                marginLayoutParams.topMargin = insetsTop
                        + getResources().getDimensionPixelSize(R.dimen.drawer_avatar_margin_top);

                // see https://github.com/android/platform_frameworks_support/blob/master/v4/api21/android/support/v4/widget/DrawerLayoutCompatApi21.java#L86
                // add DrawerLayout's default View.OnApplyWindowInsetsListener implementation
                ((DrawerLayout) v).setChildInsets(new WindowInsets(insets), insetsTop > 0);
                return insets.consumeSystemWindowInsets();
            });
        }

        mDrawerUserAvatarView.setOnClickListener(v ->
                new ThemeChangeDialogFragment().show(getSupportFragmentManager(),
                        ThemeChangeDialogFragment.TAG));
        mDrawerUsernameView = (TextView) drawerHeaderView.findViewById(R.id.drawer_username);

        // Show default avatar and login prompt if user hasn't logged in,
        // else show user's avatar and username.
        if (User.hasLoggedIn()) {
            setupDrawerUserView();
        } else {
            setupDrawerLoginPrompt();
        }

        mNavigationView.setNavigationItemSelectedListener(item -> {
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
                case R.id.help:
                    callable = this::onHelpMenuSelected;

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
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

        return null;
    }

    private Void onHelpMenuSelected() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);

        return null;
    }

    /**
     * Show default avatar and login prompt if user hasn't logged in.
     */
    private void setupDrawerLoginPrompt() {
        if (mDrawerLayout == null || mNavigationView == null
                || mDrawerHeaderBackgroundView == null
                || mDrawerUserAvatarView == null || mDrawerUsernameView == null) {
            return;
        }

        // setup default avatar
        Glide.with(this)
                .load(R.drawable.ic_drawer_avatar_placeholder)
                .signature(mDownloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                .transform(new CenterCrop(Glide.get(this).getBitmapPool()))
                .into(mDrawerUserAvatarView);

        // start LoginActivity if clicked
        mDrawerUsernameView.setText(R.string.action_login);

        mDrawerHeaderBackgroundView.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(mNavigationView);

            Intent intent = new Intent(this, LoginActivity.class);
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
                .signature(mDownloadPreferencesManager.getAvatarCacheInvalidationIntervalSignature())
                .error(R.drawable.ic_drawer_avatar_placeholder)
                .transform(new CenterCrop(Glide.get(this).getBitmapPool()))
                .into(mDrawerUserAvatarView);

        // show logout dialog if clicked
        mDrawerUsernameView.setText(User.getName());

        mDrawerHeaderBackgroundView.setOnClickListener(v ->
                new LogoutDialogFragment().show(getSupportFragmentManager(), LogoutDialogFragment.TAG));
    }

    /**
     * Subclass must have to implement {@link android.view.View.OnClickListener}
     * in order to use this method.
     */
    void setupFloatingActionButton(@DrawableRes int resId) {
        ViewGroup container = (ViewGroup) findViewById(R.id.coordinator_layout);
        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getLayoutInflater().inflate(R.layout.floating_action_button, container, false);
        container.addView(floatingActionButton);

        floatingActionButton.setOnClickListener((View.OnClickListener) this);
        floatingActionButton.setImageResource(resId);
    }

    Toolbar getToolbar() {
        return mToolbar;
    }

    public static class ThemeChangeDialogFragment extends DialogFragment {

        private static final String TAG = ThemeChangeDialogFragment.class.getSimpleName();

        ThemeManager mThemeManager;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            mThemeManager = App.getAppComponent(activity).getThemeManager();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int checkedItem = mThemeManager.getThemeIndex();
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pref_theme)
                    .setSingleChoiceItems(
                            R.array.pref_theme_entries,
                            checkedItem,
                            (dialog, which) -> {
                                // won't change theme if unchanged
                                if (which != checkedItem) {
                                    mThemeManager.applyTheme(checkedItem);
                                    mThemeManager.setThemeByIndex(checkedItem);

                                    BusProvider.get().post(new ThemeChangeEvent());
                                }
                                dismiss();
                            })
                    .create();
        }
    }

    public static class LoginPromptDialogFragment extends DialogFragment {

        private static final String TAG = LoginPromptDialogFragment.class.getSimpleName();

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
     * Show {@link LoginPromptDialogFragment} if user hasn't logged in.
     *
     * @return whether user has logged in
     */
    boolean checkUserLoggedInStatus() {
        if (!User.hasLoggedIn()) {
            new LoginPromptDialogFragment().show(getSupportFragmentManager(),
                    LoginPromptDialogFragment.TAG);

            return false;
        }

        return true;
    }

    public static class LogoutDialogFragment extends DialogFragment {

        private static final String TAG = LogoutDialogFragment.class.getSimpleName();

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
