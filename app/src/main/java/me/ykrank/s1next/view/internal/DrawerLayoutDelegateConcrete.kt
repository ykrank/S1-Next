package me.ykrank.s1next.view.internal

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.ui.internal.DrawerLayoutDelegate
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.AlipayDonate
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.github.ykrank.androidtools.widget.track.event.ThemeChangeTrackEvent
import com.google.android.material.navigation.NavigationView
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.pref.DataPreferencesManager
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.databinding.ActionViewNoticeCountBinding
import me.ykrank.s1next.databinding.NavigationViewHeaderBinding
import me.ykrank.s1next.task.AutoSignTask
import me.ykrank.s1next.util.DonateUtils
import me.ykrank.s1next.view.activity.*
import me.ykrank.s1next.view.dialog.AlipayDialogFragment
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.dialog.LogoutDialogFragment
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment
import me.ykrank.s1next.view.page.setting.SettingsActivity
import me.ykrank.s1next.viewmodel.UserViewModel
import javax.inject.Inject

/**
 * Implements the concrete UI logic for [DrawerLayoutDelegate].
 */
class DrawerLayoutDelegateConcrete(
    val activity: androidx.fragment.app.FragmentActivity,
    drawerLayout: DrawerLayout,
    navigationView: NavigationView
) : DrawerLayoutDelegate(activity, drawerLayout, navigationView), NavigationView.OnNavigationItemSelectedListener {

    private val mUser: User

    @Inject
    internal lateinit var mUserViewModel: UserViewModel

    @Inject
    internal lateinit var trackAgent: DataTrackAgent

    @Inject
    internal lateinit var mThemeManager: ThemeManager

    @Inject
    internal lateinit var mDataPreferencesManager: DataPreferencesManager

    @Inject
    internal lateinit var mAutoSignTask: AutoSignTask

    private lateinit var pmNoticeBinding: ActionViewNoticeCountBinding
    private lateinit var noteNoticeBinding: ActionViewNoticeCountBinding
    private lateinit var binding: NavigationViewHeaderBinding

    init {
        App.appComponent.inject(this)
        mUser = mUserViewModel.user
    }

    override fun setupNavDrawerItem(drawerLayout: DrawerLayout, navigationView: NavigationView) {
        setupNavDrawerHeader(drawerLayout, navigationView)
        setupNavDrawerNotice(navigationView)

        navigationView.setNavigationItemSelectedListener(this)
        setupNavDrawerItemChecked(navigationView)
        setupNavDrawerItemVisible(navigationView)
    }

    @SuppressLint("RestrictedApi")
    private fun setupNavDrawerHeader(drawerLayout: DrawerLayout, navigationView: NavigationView) {
        binding = DataBindingUtil.bind(navigationView.getHeaderView(0))!!
        binding.userViewModel = mUserViewModel

        // let status bar display over drawer if API >= 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // let DrawerLayout draw the insets area for the status bar
            mFragmentActivity.window.statusBarColor = Color.TRANSPARENT
            // add status bar height to drawer's header
            drawerLayout.setOnApplyWindowInsetsListener { v, insets ->
                val insetsTop = insets.systemWindowInsetTop

                val marginLayoutParams = binding.drawerUserAvatar.layoutParams as ViewGroup.MarginLayoutParams
                marginLayoutParams.topMargin = insetsTop + v.context.resources
                    .getDimensionPixelSize(com.github.ykrank.androidtools.R.dimen.drawer_avatar_margin_top)

                // see https://github.com/android/platform_frameworks_support/blob/master/v4/api21/android/support/v4/widget/DrawerLayoutCompatApi21.java#L86
                // add DrawerLayout's default View.OnApplyWindowInsetsListener implementation
                (v as DrawerLayout).setChildInsets(WindowInsets(insets), insetsTop > 0)
                insets.consumeSystemWindowInsets()
            }
        }


        binding.drawerHeaderBackground.setOnClickListener {
            ThemeChangeDialogFragment.showThemeChangeDialog(mFragmentActivity)
            trackAgent.post(ThemeChangeTrackEvent(true))
        }

        binding.drawerUserAvatar.setOnClickListener {
            if (mUser.isLogged) {
                UserHomeActivity.start(activity, mUser.uid!!, mUser.name!!, it)
            } else {
                binding.drawerUserName.performClick()
            }
        }

        // Starts LoginActivity if user hasn't logged in,
        // otherwise show LogoutDialogFragment.
        binding.drawerUserName.setOnClickListener { v ->
            if (!LogoutDialogFragment.showLogoutDialogIfNeeded(mFragmentActivity, mUser)) {
                closeDrawer(Runnable { LoginActivity.startLoginActivityForResultMessage(mFragmentActivity) })
            }
        }

        binding.drawerAutoSign.setOnClickListener {
            if (!mUser.isSigned) {
                mAutoSignTask.autoSign().compose(RxJavaUtil.iOSingleTransformer())
                    .subscribe({ d ->
                        mUser.isSigned = d.signed
                        App.get().toast(d.msg, Toast.LENGTH_SHORT)
                    }, { L.report(it) })
            }
        }
    }

    private fun setupNavDrawerNotice(navigationView: NavigationView) {
        pmNoticeBinding = ActionViewNoticeCountBinding.inflate(LayoutInflater.from(mFragmentActivity))!!
        noteNoticeBinding = ActionViewNoticeCountBinding.inflate(LayoutInflater.from(mFragmentActivity))!!
        navigationView.menu.findItem(R.id.menu_pms).actionView = pmNoticeBinding.root
        navigationView.menu.findItem(R.id.menu_note).actionView = noteNoticeBinding.root
        refreshNoticeMenuItem()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.userViewModel = null
    }

    /**
     * refresh label in navigation menu to show whether new pm or notice
     */
    fun refreshNoticeMenuItem() {
        pmNoticeBinding.msg = if (mDataPreferencesManager.hasNewPm) "new" else null
        noteNoticeBinding.msg = if (mDataPreferencesManager.hasNewNotice) "new" else null
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val runnable: Runnable
        when (menuItem.itemId) {
            R.id.menu_home -> runnable = Runnable { this.onHomeMenuSelected() }
            R.id.menu_favourites -> runnable = Runnable { this.onFavouritesMenuSelected() }
            R.id.menu_pms -> runnable = Runnable { this.onWhispersMenuSelected() }
            R.id.menu_note -> runnable = Runnable { this.onNoteMenuSelected() }
            R.id.menu_history -> runnable = Runnable { this.onHistoryMenuSelected() }
            R.id.menu_blacklist -> runnable = Runnable { this.onBlackListMenuSelected() }
            R.id.menu_settings -> runnable = Runnable { this.onSettingsMenuSelected() }
            R.id.menu_help -> runnable = Runnable { this.onHelpMenuSelected() }
            R.id.menu_donate -> runnable = Runnable { this.onDonateMenuSelected() }
            R.id.menu_red_envelopes -> runnable = Runnable { this.onRedEnvelopedMenuSelected() }
            else -> {
                mFragmentActivity.toast("Unknown menu item ID: " + menuItem.itemId + ".")
                return false
            }
        }
        closeDrawer(runnable)

        return false
    }

    private fun setupNavDrawerItemChecked(navigationView: NavigationView) {
        var menuItem: MenuItem? = null
        val menu = navigationView.menu
        if (mFragmentActivity is ForumActivity) {
            menuItem = menu.findItem(R.id.menu_home)
        } else if (mFragmentActivity is FavouriteListActivity) {
            menuItem = menu.findItem(R.id.menu_favourites)
        } else if (mFragmentActivity is PmActivity) {
            menuItem = menu.findItem(R.id.menu_pms)
        } else if (mFragmentActivity is NoteActivity) {
            menuItem = menu.findItem(R.id.menu_note)
        }
        // SettingsActivity and HelpActivity don't have drawer
        // so it's no need to set checked theirs MenuItem
        if (menuItem != null) {
            //TODO Now theme could not support
//            menuItem.isChecked = true
        }
    }

    private fun setupNavDrawerItemVisible(navigationView: NavigationView) {
        val menu = navigationView.menu
        if (AlipayDonate.hasInstalledAlipayClient(mFragmentActivity)) {
            menu.findItem(R.id.menu_donate)?.isVisible = true
            menu.findItem(R.id.menu_red_envelopes)?.isVisible = true
        }
    }

    private fun onHomeMenuSelected() {
        if (mFragmentActivity is ForumActivity) {
            return
        }

        ForumActivity.start(mFragmentActivity)
    }

    private fun onFavouritesMenuSelected() {
        if (mFragmentActivity is FavouriteListActivity) {
            return
        }

        // Starts FavouriteListActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(mFragmentActivity.supportFragmentManager, mUser)) {
            FavouriteListActivity.startFavouriteListActivity(mFragmentActivity)
        }
    }

    private fun onWhispersMenuSelected() {
        if (mFragmentActivity is PmActivity) {
            return
        }

        // Starts PmActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(mFragmentActivity.supportFragmentManager, mUser)) {
            PmActivity.startPmActivity(mFragmentActivity)
        }
    }

    private fun onSettingsMenuSelected() {
        SettingsActivity.startSettingsActivity(mFragmentActivity)
    }

    private fun onHelpMenuSelected() {
        HelpActivity.startHelpActivity(mFragmentActivity, mThemeManager.themeStyle)
    }

    private fun onNoteMenuSelected() {
        if (mFragmentActivity is NoteActivity) {
            return
        }

        // Starts NoteActivity if user has logged in,
        // otherwise show LoginPromptDialogFragment.
        if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(mFragmentActivity.supportFragmentManager, mUser)) {
            NoteActivity.start(mFragmentActivity)
        }
    }

    private fun onHistoryMenuSelected() {
        HistoryActivity.start(mFragmentActivity)
    }

    private fun onBlackListMenuSelected() {
        SettingsActivity.startBlackListSettingsActivity(mFragmentActivity)
    }

    private fun onDonateMenuSelected() {
        DonateUtils.alipayDonate(mFragmentActivity)
    }

    private fun onRedEnvelopedMenuSelected() {
        AlipayDialogFragment.newInstance(
            mFragmentActivity.getString(R.string.red_envelopes_copy_label),
            mFragmentActivity.getString(R.string.red_envelopes_text)
        )
            .show(mFragmentActivity.supportFragmentManager, AlipayDialogFragment.TAG)
    }
}
