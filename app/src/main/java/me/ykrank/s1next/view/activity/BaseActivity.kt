package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.CallSuper
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ActivityEvent
import com.google.common.base.Optional
import com.github.ykrank.androidtools.ui.LibBaseActivity
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegate
import com.github.ykrank.androidtools.ui.internal.DrawerLayoutDelegate
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ResourceUtil
import com.github.ykrank.androidtools.widget.RxBus
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import io.reactivex.android.schedulers.AndroidSchedulers
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.pref.DataPreferencesManager
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.view.dialog.ReportErrorDialogFragment
import me.ykrank.s1next.view.dialog.ThreadGoDialogFragment
import me.ykrank.s1next.view.event.FontSizeChangeEvent
import me.ykrank.s1next.view.event.NoticeRefreshEvent
import me.ykrank.s1next.view.event.ThemeChangeEvent
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegateImpl
import me.ykrank.s1next.view.internal.DrawerLayoutDelegateConcrete
import me.ykrank.s1next.view.internal.RequestCode
import me.ykrank.s1next.view.internal.ToolbarDelegate
import javax.inject.Inject

/**
 * A base Activity which includes the Toolbar
 * and navigation drawer amongst others.
 * Also changes theme depends on settings.
 */
abstract class BaseActivity : LibBaseActivity() {

    @Inject
    internal lateinit var mRxBus: RxBus

    @Inject
    internal lateinit var mUser: User

    @Inject
    lateinit var mGeneralPreferencesManager: GeneralPreferencesManager

    @Inject
    internal lateinit var mDownloadPreferencesManager: DownloadPreferencesManager

    @Inject
    internal lateinit var mDataPreferencesManager: DataPreferencesManager

    @Inject
    internal lateinit var mThemeManager: ThemeManager

    @Inject
    internal lateinit var trackAgent: DataTrackAgent

    private var mToolbarDelegate: ToolbarDelegate? = null
    private var drawerLayoutDelegate: DrawerLayoutDelegateConcrete? = null

    override var mDrawerIndicatorEnabled: Boolean = true

    open val isTranslucent: Boolean
        get() = false

    internal val toolbar: Optional<Toolbar>
        get() = if (mToolbarDelegate == null) {
            Optional.absent()
        } else {
            Optional.of(mToolbarDelegate!!.toolbar)
        }

    override fun attachBaseContext(newBase: Context?) {
        App.appComponent.inject(this)
        super.attachBaseContext(ResourceUtil.setScaledDensity(newBase, mGeneralPreferencesManager.fontScale))
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        // change the theme depends on preference
        if (!mThemeManager.isDefaultTheme) {
            if (isTranslucent) {
                setTheme(mThemeManager.themeTranslucentStyle)
            } else {
                setTheme(mThemeManager.themeStyle)
            }
        }


        super.onCreate(savedInstanceState)

        mRxBus.get()
            .filter { o -> o is ThemeChangeEvent || o is FontSizeChangeEvent }
            .to(AndroidRxDispose.withObservable(this, ActivityEvent.DESTROY))
            .subscribe { o ->
                window.setWindowAnimations(R.style.Animation_Recreate)
                recreate()
            }
        mRxBus.get(NoticeRefreshEvent::class.java)
            .ofType(NoticeRefreshEvent::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .to(AndroidRxDispose.withObservable(this, ActivityEvent.DESTROY))
            .subscribe { event -> refreshNoticeMenuItem(event.isNewPm, event.isNewNotice) }
    }

    override fun setTitle(title: CharSequence?) {
        if (mToolbarDelegate?.setTitle(title) == true) {
            super.setTitle("")
        } else {
            super.setTitle(title)
        }
    }

    @CallSuper
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupToolbar()
    }

    @CallSuper
    override fun setContentView(view: View) {
        super.setContentView(view)
        setupToolbar()
    }

    @CallSuper
    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
        setupToolbar()
    }


    override fun findCoordinatorLayoutAnchorDelegate(): CoordinatorLayoutAnchorDelegate? {
        return CoordinatorLayoutAnchorDelegateImpl(findViewById(R.id.coordinator_layout))
    }

    override fun findDrawerLayoutDelegate(): DrawerLayoutDelegate? {
        val drawerLayout: androidx.drawerlayout.widget.DrawerLayout? = findViewById(R.id.drawer_layout)
        if (drawerLayout != null) {
            val navigationView: NavigationView = findViewById(R.id.navigation_view)
            drawerLayoutDelegate = DrawerLayoutDelegateConcrete(this, drawerLayout, navigationView)
        } else {
            drawerLayoutDelegate = null
        }
        return drawerLayoutDelegate
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // we show thread go menu only if this Activity has drawer
        if (drawerLayoutDelegate != null) {
            menuInflater.inflate(R.menu.activity_base, menu)
            menu.findItem(R.id.menu_send_report).isVisible = L.showLog()
        }

        return super.onCreateOptionsMenu(menu)
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app drawer touch event.
        if (drawerLayoutDelegate?.onOptionsItemSelected(item) == true) {
            return true
        }

        when (item.itemId) {
            android.R.id.home -> {
                // According to https://developer.android.com/design/patterns/navigation.html
                // we should navigate to its hierarchical parent of the current screen.
                // But the hierarchical logical is too complex in our app (sub forum, link redirection),
                // so we use finish() to close the current Activity.
                // looks the newest Google Play does the same way
                finish()

                return true
            }
            R.id.menu_thread_go -> {
                ThreadGoDialogFragment().show(
                    supportFragmentManager,
                    ThreadGoDialogFragment.TAG
                )

                return true
            }
            R.id.menu_send_report -> {
                ReportErrorDialogFragment().show(supportFragmentManager, null)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //sometime java.lang.IllegalStateException·Can not perform this action after onSaveInstanceState
        try {
            super.onBackPressed()
        } catch (throwable: Throwable) {
            L.report(throwable)
        }

    }

    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        drawerLayoutDelegate?.onConfigurationChanged(newConfig)
        mThemeManager.invalidateTheme()
        mRxBus.post(ThemeChangeEvent())
    }

    /**
     * @see .startActivityForResultMessage
     * @see .setResultMessage
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.REQUEST_CODE_MESSAGE_IF_SUCCESS) {
            if (resultCode == Activity.RESULT_OK) {
                // We can't use #showShortText(String) because #onActivityResult(int, int, Intent)
                // is always invoked when current app is running in the foreground (so we are
                // unable to show a Toast if our app is running in the background).
                val msg = data?.getStringExtra(EXTRA_MESSAGE)
                if (msg != null) {
                    showShortSnackbar(msg)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        if (toolbar != null) {
            mToolbarDelegate = ToolbarDelegate(this, toolbar)
        }
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    internal fun setupNavCrossIcon() {
        mToolbarDelegate?.setupNavCrossIcon()
    }

    /**
     * @see DrawerLayoutDelegateConcrete.refreshNoticeMenuItem
     */
    fun refreshNoticeMenuItem(newPm: Boolean?, newNotice: Boolean?) {
        if (newPm != null) {
            mDataPreferencesManager.hasNewPm = newPm
        }
        if (newNotice != null) {
            mDataPreferencesManager.hasNewNotice = newNotice
        }

        drawerLayoutDelegate?.refreshNoticeMenuItem()
    }

    /**
     * Calls this method before [.onPostCreate]
     * otherwise it doesn't works.
     */
    protected fun disableDrawerIndicator() {
        mDrawerIndicatorEnabled = false
    }

    fun replaceFragmentWithBackStack(fragment: androidx.fragment.app.Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment, tag)
            .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }

    companion object {

        val EXTRA_MESSAGE = "message"

        /**
         * @see .setResultMessage
         * @see .onActivityResult
         */
        fun startActivityForResultMessage(activity: Activity, intent: Intent) {
            activity.startActivityForResult(intent, RequestCode.REQUEST_CODE_MESSAGE_IF_SUCCESS)
        }

        /**
         * Sets result message to [Activity] in order to show a short [Snackbar]
         * for this message during [.onActivityResult].
         *
         * @param message The message to show.
         * @see .startActivityForResultMessage
         * @see .onActivityResult
         */
        fun setResultMessage(activity: Activity, message: CharSequence?) {
            val intent = Intent()
            intent.putExtra(EXTRA_MESSAGE, message)
            activity.setResult(Activity.RESULT_OK, intent)
        }
    }
}
