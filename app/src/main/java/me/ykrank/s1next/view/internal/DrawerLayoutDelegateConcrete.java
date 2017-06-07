package me.ykrank.s1next.view.internal;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Toast;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.pref.DataPreferencesManager;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.databinding.ActionViewNoticeCountBinding;
import me.ykrank.s1next.databinding.NavigationViewHeaderBinding;
import me.ykrank.s1next.extension.ContextExtensionKt;
import me.ykrank.s1next.task.AutoSignTask;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.activity.FavouriteListActivity;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.view.activity.HelpActivity;
import me.ykrank.s1next.view.activity.HistoryActivity;
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
    @Inject
    UserViewModel mUserViewModel;
    @Inject
    DataTrackAgent trackAgent;
    @Inject
    ThemeManager mThemeManager;
    @Inject
    DataPreferencesManager mDataPreferencesManager;
    @Inject
    AutoSignTask mAutoSignTask;

    private ActionViewNoticeCountBinding pmNoticeBinding, noteNoticeBinding;
    private NavigationViewHeaderBinding binding;

    public DrawerLayoutDelegateConcrete(FragmentActivity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        super(activity, drawerLayout, navigationView);
        App.getAppComponent().inject(this);
        mUser = mUserViewModel.getUser();
    }

    @Override
    protected void setupNavDrawerItem(DrawerLayout drawerLayout, NavigationView navigationView) {
        setupNavDrawerHeader(drawerLayout, navigationView);
        setupNavDrawerNotice(navigationView);

        navigationView.setNavigationItemSelectedListener(this);
        setupNavDrawerItemChecked(navigationView);
    }

    private void setupNavDrawerHeader(DrawerLayout drawerLayout, NavigationView navigationView) {
        binding = DataBindingUtil.bind(navigationView.getHeaderView(0));
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
                UserHomeActivity.start(v.getContext(), mUser.getUid(), mUser.getName(), v);
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

        binding.drawerAutoSign.setOnClickListener(v -> {
            if (!mUser.isSigned()) {
                mAutoSignTask.autoSign().compose(RxJavaUtil.iOTransformer())
                        .subscribe(d -> {
                            mUser.setSigned(d.getSigned());
                            ContextExtensionKt.toast(App.get(), d.getMsg(), Toast.LENGTH_SHORT);
                        }, L::report);
            }
        });
    }

    private void setupNavDrawerNotice(NavigationView navigationView) {
        pmNoticeBinding = ActionViewNoticeCountBinding.inflate(LayoutInflater.from(mFragmentActivity));
        noteNoticeBinding = ActionViewNoticeCountBinding.inflate(LayoutInflater.from(mFragmentActivity));
        navigationView.getMenu().findItem(R.id.menu_pms).setActionView(pmNoticeBinding.getRoot());
        navigationView.getMenu().findItem(R.id.menu_note).setActionView(noteNoticeBinding.getRoot());
        refreshNoticeMenuItem();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.setUserViewModel(null);
    }

    /**
     * refresh label in navigation menu to show whether new pm or notice
     */
    @Override
    public void refreshNoticeMenuItem() {
        pmNoticeBinding.setMsg(mDataPreferencesManager.getHasNewPm() ? "new" : null);
        noteNoticeBinding.setMsg(mDataPreferencesManager.getHasNewNotice() ? "new" : null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Runnable runnable;
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                runnable = this::onHomeMenuSelected;
                break;
            case R.id.menu_favourites:
                runnable = this::onFavouritesMenuSelected;
                break;
            case R.id.menu_pms:
                runnable = this::onWhispersMenuSelected;
                break;
            case R.id.menu_note:
                runnable = this::onNoteMenuSelected;
                break;
            case R.id.menu_history:
                runnable = this::onHistoryMenuSelected;
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
        closeDrawer(runnable);

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
            menuItem = menu.findItem(R.id.menu_pms);
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
        HelpActivity.startHelpActivity(mFragmentActivity, mThemeManager.getThemeStyle());
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

    private void onHistoryMenuSelected() {
        HistoryActivity.start(mFragmentActivity);
    }
}
