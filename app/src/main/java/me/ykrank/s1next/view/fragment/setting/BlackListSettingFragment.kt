package me.ykrank.s1next.view.fragment.setting

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.ListView
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Single
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.BlackListDbWrapper
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.FragmentBlacklistBinding
import me.ykrank.s1next.view.activity.DarkRoomActivity
import me.ykrank.s1next.view.adapter.BlackListCursorListViewAdapter
import me.ykrank.s1next.view.dialog.BlacklistDialogFragment
import me.ykrank.s1next.view.fragment.BaseFragment
import me.ykrank.s1next.view.internal.RequestCode
import java.util.*

class BlackListSettingFragment : BaseFragment() {

    private lateinit var mListView: ListView
    private lateinit var mListViewAdapter: BlackListCursorListViewAdapter

    private val mActionModeCallback = object : AbsListView.MultiChoiceModeListener {

        override fun onItemCheckedStateChanged(mode: android.view.ActionMode, position: Int, id: Long, checked: Boolean) {

        }

        // Called when the action mode is created; startActionMode() was called
        override fun onCreateActionMode(mode: android.view.ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.actionmode_blacklist_edit, menu)
            return true
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        override fun onPrepareActionMode(mode: android.view.ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: android.view.ActionMode, item: MenuItem): Boolean {
            val checklist = mListView.checkedItemPositions
            when (item.itemId) {
                R.id.menu_add -> {
                    add()
                    return true
                }
                R.id.menu_edit -> {
                    var blackList: BlackList? = null
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackList = mListViewAdapter.getItem(checklist.keyAt(i))
                            break
                        }
                    }
                    val dialogFragment1 = BlacklistDialogFragment.newInstance(blackList)
                    dialogFragment1.setTargetFragment(this@BlackListSettingFragment, RequestCode.REQUEST_CODE_BLACKLIST)
                    dialogFragment1.show(fragmentManager, BlackListSettingFragment::class.java.name)
                    return true
                }
                R.id.menu_delete -> {
                    val blackLists = ArrayList<BlackList>()
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackLists.add(mListViewAdapter.getItem(checklist.keyAt(i)))
                        }
                    }
                    BlackListDbWrapper.getInstance().delBlackLists(blackLists)
                    load()
                    return true
                }
                R.id.menu_all -> {
                    for (i in 0 until mListView.count) {
                        mListView.setItemChecked(i, true)
                    }
                    return true
                }
                R.id.menu_clear -> {
                    for (i in 0 until mListView.count) {
                        mListView.setItemChecked(i, false)
                    }
                    return true
                }
                else -> return false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: android.view.ActionMode) {

        }
    }

    internal val sourceObservable: Single<Cursor>
        get() = BlackListDbWrapper.getInstance().blackListCursor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentBlacklistBinding>(inflater,
                R.layout.fragment_blacklist, container, false)
        mListView = binding.listview
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListViewAdapter = BlackListCursorListViewAdapter(activity)
        mListView.adapter = mListViewAdapter
        mListView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
        mListView.setMultiChoiceModeListener(mActionModeCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_blacklist, menu)
        menu?.findItem(R.id.menu_refresh)?.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                add()
                return true
            }
            R.id.menu_refresh -> {
                load()
                return true
            }
            R.id.menu_dark_room -> {
                activity?.let {
                    DarkRoomActivity.start(it)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Starts to load new data.
     */
    private fun load() {
        sourceObservable
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({ mListViewAdapter.changeCursor(it) },
                        { throwable -> L.e("S1next", throwable) })
    }

    override fun onPause() {
        mListViewAdapter.changeCursor(null)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.REQUEST_CODE_BLACKLIST) {
            if (resultCode == Activity.RESULT_OK) {
                val blackList = data?.getParcelableExtra<BlackList>(BlacklistDialogFragment.BLACKLIST_TAG)
                if (blackList != null) {
                    BlackListDbWrapper.getInstance().saveBlackList(blackList)
                    load()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun add() {
        val dialogFragment = BlacklistDialogFragment.newInstance(null)
        dialogFragment.setTargetFragment(this@BlackListSettingFragment, RequestCode.REQUEST_CODE_BLACKLIST)
        dialogFragment.show(fragmentManager, BlackListSettingFragment::class.java.name)
    }

    companion object {
        val TAG = BlackListSettingFragment::class.java.name

        fun newInstance(): BlackListSettingFragment {
            return BlackListSettingFragment()
        }
    }
}
