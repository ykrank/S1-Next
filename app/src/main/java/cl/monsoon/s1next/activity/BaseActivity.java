package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.signature.StringSignature;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.SettingsFragment;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.MyOkHttpClient;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.DateUtil;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.widget.MyRecyclerView;
import cl.monsoon.s1next.widget.StateListDrawableWithTint;

/**
 * A base Activity which includes the toolbar tweaks,
 * drop-down navigation, navigation drawer amongst others.
 * Also change theme depends on settings.
 * <p>
 * This base activity Implement the required
 * {@link android.widget.AdapterView.OnItemSelectedListener}
 * interface for Spinner to switch between views.
 */
public abstract class BaseActivity extends ActionBarActivity
        implements User.OnLogoutListener,
        AdapterView.OnItemSelectedListener,
        ToolbarInterface.SpinnerInteractionCallback {

    private Toolbar mToolbar;

    /**
     * The serialization (saved instance state) Bundle key representing
     * the position of the selected spinner item.
     */
    private static final String STATE_SPINNER_SELECTED_POSITION = "selected_position";

    /**
     * Store the selected Spinner position after restore save instance.
     */
    private int mSelectedPosition = 0;

    private Spinner mSpinner;

    /**
     * We enable translucent system bars if API >= 19.
     * When API >= 19, we use a fake status bar (a view with background)
     * to represent the status bar color.
     * When API < 19, the fake status bar has 0dp height.
     */
    private View mToolbarWithFakeStatusbar;
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

    private View mDrawerTopBackgroundView;
    private ImageView mDrawerUserAvatarView;
    private TextView mDrawerUsernameView;

    private FloatingActionButton mFloatingActionButton;

    /**
     * Either {@link cl.monsoon.s1next.fragment.SettingsFragment#ACTION_CHANGE_THEME}
     * or {@link cl.monsoon.s1next.fragment.SettingsFragment#ACTION_CHANGE_FONT_SIZE}.
     */
    private BroadcastReceiver mRecreateActivityReceiver;
    private BroadcastReceiver mUserLoginStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Config.isDefaultApplicationTheme()) {
            setTheme(Config.getCurrentTheme());
        }

        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SettingsFragment.ACTION_CHANGE_THEME);
        intentFilter.addAction(SettingsFragment.ACTION_CHANGE_FONT_SIZE);
        // recreate this Activity when night mode or font size setting changes
        mRecreateActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BaseActivity.this instanceof SettingsActivity
                        && intent.getAction().equals(SettingsFragment.ACTION_CHANGE_FONT_SIZE)) {
                    return;
                }

                recreate();
            }
        };
        registerReceiver(mRecreateActivityReceiver, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(User.ACTION_USER_LOGIN);
        intentFilter.addAction(User.ACTION_USER_LOGOUT_OR_EXPIRATION);
        // change drawer's top area depends on user's login status
        mUserLoginStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(User.ACTION_USER_LOGIN)) {
                    setupDrawerUserView();
                } else {
                    setupDrawerLoginPrompt();
                }
            }
        };
        registerReceiver(mUserLoginStatusReceiver, intentFilter);

        if (savedInstanceState != null) {
            mSelectedPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_POSITION);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SPINNER_SELECTED_POSITION, mSelectedPosition);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpToolbar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mRecreateActivityReceiver);
        unregisterReceiver(mUserLoginStatusReceiver);
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
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    super.onBackPressed();
                }

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
                // designate a ToolBar as the ActionBar
                setSupportActionBar(mToolbar);
            }
        }
    }

    /**
     * Implement {@link android.widget.AdapterView.OnItemSelectedListener}.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Implement {@link ToolbarInterface.SpinnerInteractionCallback}.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void setupToolbarDropDown(List<? extends CharSequence> dropDownItemList) {
        if (mSpinner == null) {
            setTitle(null);

            // add Spinner (drop down) to Toolbar
            LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, mToolbar, true);
            mSpinner = (Spinner) mToolbar.findViewById(R.id.spinner);

            if (!Config.isS1Theme() && !Config.isDarkTheme()) {
                int colorAccent = Config.getColorAccent();
                int[][] states =
                        new int[][]{
                                new int[]{android.R.attr.state_enabled, android.R.attr.state_focused},
                                new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed},
                                new int[]{}
                        };
                int[] colors = new int[]{colorAccent, colorAccent, Color.TRANSPARENT};
                ColorStateList colorStateList = new ColorStateList(states, colors);

                mSpinner.setBackgroundDrawable(
                        new StateListDrawableWithTint(
                                getResources().getDrawable(R.drawable.abc_spinner_mtrl_am_alpha),
                                colorStateList,
                                PorterDuff.Mode.SRC_ATOP));
            }
            // set Listener to switch between views
            mSpinner.setOnItemSelectedListener(this);

            // We disable clickable in Spinner
            // and let its parents LinearLayout to handle
            // click event in order to increase clickable area.
            View spinnerView = mToolbar.findViewById(R.id.toolbar_layout);
            spinnerView.setOnClickListener(v -> mSpinner.performClick());
        }

        mSpinner.setAdapter(getSpinnerAdapter(dropDownItemList));
        // invalid index when user's login status has changed
        if (mSpinner.getAdapter().getCount() - 1 < mSelectedPosition) {
            mSpinner.setSelection(0, false);
        } else {
            mSpinner.setSelection(mSelectedPosition, false);
        }
    }

    BaseAdapter getSpinnerAdapter(List<? extends CharSequence> dropDownItemList) {
        throw new UnsupportedOperationException("This method hasn't been implemented.");
    }

    void setupFloatingActionButton(@DrawableRes int resId) {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        // subclass need to implement android.view.View.OnClickListener
        mFloatingActionButton.setOnClickListener(ObjectUtil.cast(this, View.OnClickListener.class));
        mFloatingActionButton.setImageResource(resId);
        mFloatingActionButton.setVisibility(View.VISIBLE);
    }

    /**
     * Also enable {@link cl.monsoon.s1next.activity.BaseActivity#mFloatingActionButton}
     * auto show/hide effect.
     */
    public void enableToolbarAndFabAutoHideEffect(MyRecyclerView myRecyclerView, @Nullable RecyclerView.OnScrollListener onScrollListener) {
        mToolbarWithFakeStatusbar = findViewById(R.id.toolbar_with_fake_statusbar);
        mToolbarAutoHideMinY = ResourceUtil.getToolbarHeight(this);

        myRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

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

                // myRecyclerView.computeVerticalScrollOffset() may cause poor performance
                // so we also check mIsToolbarShown though we will do it later (over showOrHideToolbarAndFab(boolean))
                if (mIsToolbarShown
                        && dy > 0
                        && myRecyclerView.computeVerticalScrollOffset() >= mToolbarAutoHideMinY) {
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

        if (mFloatingActionButton != null) {
            if (show) {
                mFloatingActionButton.show();
            } else {
                mFloatingActionButton.hide();
            }
        }
    }

    private void onToolbarAutoShowOrHide(boolean show) {
        if (show) {
            mToolbarWithFakeStatusbar.animate()
                    .alpha(1)
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator());
        } else {
            mToolbarWithFakeStatusbar.animate()
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
        if (User.isLoggedIn()) {
            setupDrawerUserView();
        } else {
            setupDrawerLoginPrompt();
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
    void setupDrawerUserView() {
        if (mDrawerTopBackgroundView == null
                || mDrawerUserAvatarView == null || mDrawerUsernameView == null) {
            return;
        }

        // setup user's avatar
        Glide.with(this)
                .load(Api.getUrlAvatarMedium(User.getUid()))
                .signature(new StringSignature(DateUtil.getDayWithYear()))
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

    void setNavDrawerEnabled(boolean enabled) {
        mHasNavDrawer = enabled;
    }

    void setNavDrawerIndicatorEnabled(boolean enabled) {
        mHasNavDrawerIndicator = enabled;
    }

    public static class ThemeChangeDialog extends DialogFragment {

        private static final String TAG = "theme_change_dialog";

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());

            int checkedItem =
                    Integer.parseInt(
                            sharedPreferences.getString(
                                    SettingsFragment.PREF_KEY_THEME,
                                    MyApplication.getContext()
                                            .getString(R.string.pref_theme_default_value)));
            return
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.pref_theme)
                            .setSingleChoiceItems(
                                    R.array.pref_theme_entries,
                                    checkedItem,
                                    (dialog, which) -> {
                                        Runnable runnable = null;
                                        // won't change theme if unchanged
                                        if (which != checkedItem) {
                                            sharedPreferences.edit()
                                                    .putString(
                                                            SettingsFragment.PREF_KEY_THEME,
                                                            String.valueOf(which)).apply();
                                            Config.setCurrentTheme(sharedPreferences);

                                            // We use MyApplication.getContext() instead of getActivity()
                                            // in order to avoid NullPointerException when out of scope.
                                            runnable = () -> MyApplication.getContext()
                                                    .sendBroadcast(
                                                            new Intent(SettingsFragment.ACTION_CHANGE_THEME));
                                        }
                                        dismiss();
                                        ObjectUtil.cast(
                                                getActivity(),
                                                BaseActivity.class).closeDrawer(runnable);
                                    })
                            .create();
        }
    }

    public static class LogoutDialog extends DialogFragment {

        private static final String TAG = "log_out_dialog";

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_message_log_out)
                            .setPositiveButton(
                                    android.R.string.ok,
                                    (dialog, which) ->
                                            ObjectUtil.cast(
                                                    getActivity(),
                                                    User.OnLogoutListener.class).onLogout())
                            .setNegativeButton(android.R.string.cancel, null)
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
