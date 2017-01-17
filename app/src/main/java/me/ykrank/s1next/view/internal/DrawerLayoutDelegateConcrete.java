package me.ykrank.s1next.view.internal;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowInsets;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.databinding.NavigationViewHeaderBinding;
import me.ykrank.s1next.view.activity.FavouriteListActivity;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.view.activity.HelpActivity;
import me.ykrank.s1next.view.activity.LoginActivity;
import me.ykrank.s1next.view.activity.NoteActivity;
import me.ykrank.s1next.view.activity.PmActivity;
import me.ykrank.s1next.view.activity.SettingsActivity;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment;
import me.ykrank.s1next.view.dialog.LogoutDialogFragment;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.ThemeChangeTrackEvent;

/**
 * Implements the concrete UI logic for {@link DrawerLayoutDelegate}.
 */
public final class DrawerLayoutDelegateConcrete extends DrawerLayoutDelegate
        implements NavigationView.OnNavigationItemSelectedListener {

    private final User mUser;
    private final UserViewModel mUserViewModel;
    private DataTrackAgent trackAgent;

    public DrawerLayoutDelegateConcrete(FragmentActivity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        super(activity, drawerLayout, navigationView);
        trackAgent = App.getAppComponent().getDataTrackAgent();
        mUserViewModel = App.getAppComponent().getUserViewModel();
        mUser = mUserViewModel.getUser();
    }

    @Override
    protected void setupNavDrawerItem(DrawerLayout drawerLayout, NavigationView navigationView) {
        setupNavDrawerHeader(drawerLayout, navigationView);

        navigationView.setNavigationItemSelectedListener(this);
        setupNavDrawerItemChecked(navigationView);
    }

    private void setupNavDrawerHeader(DrawerLayout drawerLayout, NavigationView navigationView) {
        NavigationViewHeaderBinding binding = DataBindingUtil.bind(navigationView.getHeaderView(0));
        binding.setUserViewModel(mUserViewModel);

        // let status bar display over drawer if API >= 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // let DrawerLayout draw the insets area for the status bar
            mFragmentActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            // add status bar height to drawer's header
            drawerLayout.setOnApplyWindowInsetsListener((v, insets) -> {
                int insetsTop = insets.getSystemWindowInsetTop();

                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                        binding.drawerUserAvatar.getLayoutParams();
                marginLayoutParams.topMargin = insetsTop + v.getContext().getResources()
                        .getDimensionPixelSize(R.dimen.drawer_avatar_margin_top);

                // see https://github.com/android/platform_frameworks_support/blob/master/v4/api21/android/support/v4/widget/DrawerLayoutCompatApi21.java#L86
                // add DrawerLayout's default View.OnApplyWindowInsetsListener implementation
                ((DrawerLayout) v).setChildInsets(new WindowInsets(insets), insetsTop > 0);
                return insets.consumeSystemWindowInsets();
            });
        }


        binding.drawerHeaderBackground.setOnClickListener(v -> {
            ThemeChangeDialogFragment.showThemeChangeDialog(mFragmentActivity);
            trackAgent.post(new ThemeChangeTrackEvent(true));
        });

        binding.drawerUserAvatar.setOnClickListener(v -> {
            if (mUser.isLogged()) {
                UserHomeActivity.start(v.getContext(), mUser.getUid(), mUser.getName());
            } else {
                binding.drawerUserName.performClick();
            }
        });

        // Starts LoginActivity if user hasn't logged in,
        // otherwise show LogoutDialogFragment.
        binding.drawerUserName.setOnClickListener(v -> {
            if (!LogoutDialogFragment.showLogoutDialogIfNeeded(mFragmentActivity, mUser)) {
                closeDrawer(() -> LoginActivity.startLoginActivityForResultMessage(mFragmentActivity));
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Runnable runnable;
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                runnable = this::onHomeMenuSelected;
                break;
            case R.id.menu_favourites:
                runnable = this::onFavouritesMenuSelected;
                break;
            case R.id.menu_whisper:
                runnable = this::onWhispersMenuSelected;
                break;
            case R.id.menu_note:
                runnable = this::onNoteMenuSelected;
                break;
            case R.id.menu_settings:
                runnable = this::onSettingsMenuSelected;
                break;
            case R.id.menu_help:
                runnable = this::onHelpMenuSelected;
                break;
            default:
                throw new IllegalStateException("Unknown menu item ID: " + menuItem.getItemId() + ".");
        }
        closeDrawer(runnable::run);

        return false;
    }

    private void setupNavDrawerItemChecked(NavigationView navigationView) {
        MenuItem menuItem = null;
        Menu menu = navigationView.getMenu();
        if (mFragmentActivity instanceof ForumActivity) {
            menuItem = menu.findItem(R.id.menu_home);
        } else if (mFragmentActivity instanceof FavouriteListActivity) {
            menuItem = menu.findItem(R.id.menu_favourites);
        } else if (mFragmentActivity instanceof PmActivity) {
            menuItem = menu.findItem(R.id.menu_whisper);
        } else if (mFragmentActivity instanceof NoteActivity) {
            menuItem = menu.findItem(R.id.menu_note);
        }
        // SettingsActivity and HelpActivity don't have drawer
        // so it's no need to set checked theirs MenuItem
        if (menuItem != null) {
            menuItem.setChecked(true);
        }
    }

    private void onHomeMenuSelected() {
        if (mFragmentActivity instanceof ForumActivity) {
            return;
        }

        ForumActivity.start(mFragmentActivity);
    }

    private void onFavouritesMenuSelected() {
        if (mFragmentActivity instanceof FavouriteListActivity) {
            return;
        }

        // Starts FavouriteListActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(mFragmentActivity, mUser)) {
            FavouriteListActivity.startFavouriteListActivity(mFragmentActivity);
        }
    }

    private void onWhispersMenuSelected() {
        if (mFragmentActivity instanceof PmActivity) {
            return;
        }

        // Starts PmActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(mFragmentActivity, mUser)) {
            PmActivity.startPmActivity(mFragmentActivity);
        }
    }

    private void onSettingsMenuSelected() {
        SettingsActivity.startSettingsActivity(mFragmentActivity);
    }

    private void onHelpMenuSelected() {
        HelpActivity.startHelpActivity(mFragmentActivity);
    }

    private void onNoteMenuSelected() {
        if (mFragmentActivity instanceof NoteActivity) {
            return;
        }

        // Starts NoteActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(mFragmentActivity, mUser)) {
            NoteActivity.start(mFragmentActivity);
        }
    }
}
