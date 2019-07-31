package me.ykrank.s1next.view.activity

import android.app.Activity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import android.view.View
import android.widget.AdapterView
import com.google.common.base.Optional
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager
import me.ykrank.s1next.databinding.ToolbarSpinnerBinding
import me.ykrank.s1next.view.fragment.ForumFragment
import me.ykrank.s1next.view.internal.RequestCode
import me.ykrank.s1next.view.internal.ToolbarDropDownInterface
import me.ykrank.s1next.viewmodel.DropDownItemListViewModel
import javax.inject.Inject

/**
 * An Activity shows the forum groups.
 *
 *
 * This Activity has Spinner in Toolbar to switch between different forum groups.
 */
class ForumActivity : BaseActivity(), ToolbarDropDownInterface.Callback, AdapterView.OnItemSelectedListener {

    @Inject
    internal lateinit var mReadProgressPrefManager: ReadProgressPreferencesManager

    private var mToolbarSpinnerBinding: ToolbarSpinnerBinding? = null

    /**
     * Stores selected Spinner position.
     */
    private var mSelectedPosition = 0

    private lateinit var onItemSelectedListener: ToolbarDropDownInterface.OnItemSelectedListener

    private lateinit var fragment: ForumFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)

        setContentView(R.layout.activity_base)

        val fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            restoreFromInterrupt()

            fragment = ForumFragment()
            fragmentManager.beginTransaction().add(R.id.frame_layout, fragment, ForumFragment.TAG)
                    .commit()
        } else {
            mSelectedPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_POSITION)
            fragment = fragmentManager.findFragmentByTag(ForumFragment.TAG) as ForumFragment
        }

        onItemSelectedListener = fragment
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (fragment == null) {
            fragment = supportFragmentManager
                    .findFragmentByTag(ForumFragment.TAG) as ForumFragment
        }
        fragment.startSwipeRefresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    showShortSnackbar(data.getStringExtra(BaseActivity.Companion.EXTRA_MESSAGE))
                    if (fragment != null) {
                        fragment.forceSwipeRefresh()
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putInt(STATE_SPINNER_SELECTED_POSITION, mSelectedPosition)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        mSelectedPosition = position
        onItemSelectedListener.onToolbarDropDownItemSelected(mSelectedPosition)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun setupToolbarDropDown(dropDownItemList: List<CharSequence>) {
        val binding: ToolbarSpinnerBinding
        if (mToolbarSpinnerBinding == null) {
            setTitle("")

            // add Spinner to Toolbar
            binding = DataBindingUtil.inflate<ToolbarSpinnerBinding>(layoutInflater,
                    R.layout.toolbar_spinner, toolbar.get(), true)
            binding.spinner.onItemSelectedListener = this
            // let spinner's parent to handle clicking event in order
            // to increase spinner's clicking area.
            binding.spinnerContainer.setOnClickListener { v -> binding.spinner.performClick() }
            binding.dropDownItemListViewModel = DropDownItemListViewModel()

            mToolbarSpinnerBinding = binding
        } else {
            binding = mToolbarSpinnerBinding as ToolbarSpinnerBinding
        }

        val viewModel = binding.dropDownItemListViewModel
        viewModel?.let {
            it.selectedItemPosition = mSelectedPosition
            it.dropDownItemList.clear()
            it.dropDownItemList.addAll(dropDownItemList)
        }

    }

    private fun restoreFromInterrupt() {
        Single.just(0)
                .map { Optional.fromNullable(mReadProgressPrefManager.lastReadProgress) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .doFinally { mReadProgressPrefManager.saveLastReadProgress(null) }
                .subscribe({ readProgress ->
                    if (readProgress.isPresent()) {
                        PostListActivity.start(this@ForumActivity, readProgress.get())
                    }
                }, { L.report(it) })
    }

    companion object {

        /**
         * The serialization (saved instance state) Bundle key representing
         * the position of the selected spinner item.
         */
        private val STATE_SPINNER_SELECTED_POSITION = "spinner_selected_position"

        fun start(activity: Activity) {
            val intent = Intent(activity, ForumActivity::class.java)
            // if this activity is not part of this app's task
            if (NavUtils.shouldUpRecreateTask(activity, intent)) {
                // finish all our Activities in that app
                ActivityCompat.finishAffinity(activity)
                // create a new task when navigating up with
                // a synthesized back stack
                TaskStackBuilder.create(activity)
                        .addNextIntentWithParentStack(intent)
                        .startActivities()
            } else {
                // back to ForumActivity (main Activity)
                NavUtils.navigateUpTo(activity, intent)
            }
        }
    }
}
