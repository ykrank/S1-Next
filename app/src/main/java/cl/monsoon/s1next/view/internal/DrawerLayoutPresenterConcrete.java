package cl.monsoon.s1next.view.internal;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowInsets;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.databinding.NavigationViewHeaderBinding;
import cl.monsoon.s1next.view.activity.FavouriteListActivity;
import cl.monsoon.s1next.view.activity.ForumActivity;
import cl.monsoon.s1next.view.activity.HelpActivity;
import cl.monsoon.s1next.view.activity.LoginActivity;
import cl.monsoon.s1next.view.activity.SettingsActivity;
import cl.monsoon.s1next.view.dialog.LoginPromptDialogFragment;
import cl.monsoon.s1next.view.dialog.LogoutDialogFragment;
import cl.monsoon.s1next.view.dialog.ThemeChangeDialogFragment;
import cl.monsoon.s1next.viewmodel.UserViewModel;

/**
 * Implement the concrete UI logic for {@link DrawerLayoutPresenter}.
 */
public final class DrawerLayoutPresenterConcrete extends DrawerLayoutPresenter
        implements NavigationView.OnNavigationItemSelectedListener {

    private final User mUser;
    private final UserViewModel mUserViewModel;

    public DrawerLayoutPresenterConcrete(FragmentActivity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        super(activity, drawerLayout, navigationView);
        mUserViewModel = App.getAppComponent(activity).getUserViewModel();
        mUser = mUserViewModel.getUser();
    }

    @Override
    protected void setupNavDrawerItem(DrawerLayout drawerLayout, NavigationView navigationView) {
        setupNavDrawerHeader(drawerLayout, navigationView);

        navigationView.setNavigationItemSelectedListener(this);
        setupNavDrawerItemChecked(navigationView);
    }

    private void setupNavDrawerHeader(DrawerLayout drawerLayout, NavigationView navigationView) {
        NavigationViewHeaderBinding binding = DataBindingUtil.bind(navigationView.findViewById(
                R.id.drawer_header));
        binding.setUserViewModel(mUserViewModel);

        // let status bar display over drawer if API >= 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // let DrawerLayout draw the insets area for the status bar
            mFragmentActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            // add status bar height to drawer's header
            drawerLayout.setOnApplyWindowInsetsListener((v, insets) -> {
                int insetsTop = insets.getSystemWindowInsetTop();

                binding.drawerHeaderBackground.getLayoutParams().height = insetsTop
                        + mFragmentActivity.getResources().getDimensionPixelSize(
                        R.dimen.drawer_top_height);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                        binding.drawerUserAvatar.getLayoutParams();
                marginLayoutParams.topMargin = insetsTop
                        + mFragmentActivity.getResources().getDimensionPixelSize(
                        R.dimen.drawer_avatar_margin_top);

                // see https://github.com/android/platform_frameworks_support/blob/master/v4/api21/android/support/v4/widget/DrawerLayoutCompatApi21.java#L86
                // add DrawerLayout's default View.OnApplyWindowInsetsListener implementation
                ((DrawerLayout) v).setChildInsets(new WindowInsets(insets), insetsTop > 0);
                return insets.consumeSystemWindowInsets();
            });
        }

        // Starts LoginActivity if user hasn't logged in,
        // otherwise show LogoutDialogFragment.
        binding.drawerHeaderBackground.setOnClickListener(v -> {
            if (!LogoutDialogFragment.showLogoutDialogIfNeed(mFragmentActivity, mUser)) {
                closeDrawer(() -> {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    v.getContext().startActivity(intent);
                });
            }
        });

        binding.drawerUserAvatar.setOnClickListener(v ->
                ThemeChangeDialogFragment.showThemeChangeDialog(mFragmentActivity));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Runnable runnable;
        switch (menuItem.getItemId()) {
            case R.id.home:
                runnable = this::onHomeMenuSelected;

                break;
            case R.id.favourites:
                runnable = this::onFavouritesMenuSelected;

                break;
            case R.id.settings:
                runnable = this::onSettingsMenuSelected;

                break;
            case R.id.help:
                runnable = this::onHelpMenuSelected;

                break;
            default:
                runnable = null;
        }

        if (runnable != null) {
            closeDrawer(runnable::run);

            return true;
        }

        return false;
    }

    private void setupNavDrawerItemChecked(NavigationView navigationView) {
        MenuItem menuItem = null;
        Menu menu = navigationView.getMenu();
        if (mFragmentActivity instanceof ForumActivity) {
            menuItem = menu.findItem(R.id.home);
        } else if (mFragmentActivity instanceof FavouriteListActivity) {
            menuItem = menu.findItem(R.id.favourites);
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

        Intent intent = new Intent(mFragmentActivity, ForumActivity.class);
        // if this activity is not part of this app's task
        if (NavUtils.shouldUpRecreateTask(mFragmentActivity, intent)) {
            // finish all our Activities in that app
            ActivityCompat.finishAffinity(mFragmentActivity);
            // create a new task when navigating up with
            // a synthesized back stack
            TaskStackBuilder.create(mFragmentActivity)
                    .addNextIntentWithParentStack(intent)
                    .startActivities();
        } else {
            // back to ForumActivity (main Activity)
            NavUtils.navigateUpTo(mFragmentActivity, intent);
        }
    }

    private void onFavouritesMenuSelected() {
        if (mFragmentActivity instanceof FavouriteListActivity) {
            return;
        }

        // Starts FavouriteListActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeed(mFragmentActivity, mUser)) {
            Intent intent = new Intent(mFragmentActivity, FavouriteListActivity.class);
            mFragmentActivity.startActivity(intent);
        }
    }

    private void onSettingsMenuSelected() {
        SettingsActivity.startSettingsActivity(mFragmentActivity);
    }

    private void onHelpMenuSelected() {
        HelpActivity.startHelpActivity(mFragmentActivity);
    }
}
