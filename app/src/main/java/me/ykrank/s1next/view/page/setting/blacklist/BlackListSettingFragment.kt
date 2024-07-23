package me.ykrank.s1next.view.page.setting.blacklist

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.util.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.FragmentBlacklistBinding
import me.ykrank.s1next.view.activity.DarkRoomActivity
import me.ykrank.s1next.view.dialog.LoadBlackListFromWebDialogFragment
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.fragment.BaseFragment
import me.ykrank.s1next.view.page.setting.SettingsActivity

class BlackListSettingFragment : BaseFragment(), DialogInterface.OnDismissListener {

    private lateinit var mListView: ListView
    private lateinit var mListViewAdapter: BlackListCursorListViewAdapter

    private val mActionModeCallback = object : AbsListView.MultiChoiceModeListener {

        override fun onItemCheckedStateChanged(
            mode: android.view.ActionMode,
            position: Int,
            id: Long,
            checked: Boolean
        ) {

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
                    showDialog(arrayListOf())
                    return true
                }

                R.id.menu_edit -> {
                    val blackList = ArrayList<BlackList>()
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackList.add(mListViewAdapter.getItem(checklist.keyAt(i)))
                        }
                    }
                    showDialog(blackList)
                    return true
                }

                R.id.menu_delete -> {
                    val blackLists = ArrayList<BlackList>()
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackLists.add(mListViewAdapter.getItem(checklist.keyAt(i)))
                        }
                    }
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            BlackListBiz.getInstance().delBlackLists(blackLists)
                        }
                        load()
                    }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentBlacklistBinding>(
            inflater,
            R.layout.fragment_blacklist, container, false
        )
        mListView = binding.listview
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListViewAdapter = BlackListCursorListViewAdapter(requireActivity())
        mListView.adapter = mListViewAdapter
        mListView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
        mListView.setMultiChoiceModeListener(mActionModeCallback)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_blacklist, menu)
        menu.findItem(R.id.menu_refresh)?.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                showDialog(arrayListOf())
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

            R.id.menu_black_word -> {
                activity?.let {
                    SettingsActivity.startBlackWordSettingsActivity(it)
                }
                return true
            }

            R.id.menu_load_from_web -> {
                childFragmentManager.apply {
                    if (!LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(this, mUser)) {
                        val dialogFragment =
                            LoadBlackListFromWebDialogFragment.newInstance { load() }
                        dialogFragment.show(this, LoadBlackListFromWebDialogFragment.TAG)
                    }
                }
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        mListViewAdapter.notifyDataSetChanged()
    }

    /**
     * Starts to load new data.
     */
    private fun load() {
        lifecycleScope.launch(L.report) {
            withContext(Dispatchers.IO) {
                BlackListBiz.getInstance().blackListCursor
            }.apply {
                mListViewAdapter.changeCursor(this)
            }
        }
    }

    override fun onPause() {
        mListViewAdapter.changeCursor(null)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun showDialog(blackList: ArrayList<BlackList>) {
        val dialogFragment = BlacklistDialogFragment.newInstance(blackList) {
            if (it.size > 0) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        BlackListBiz.getInstance().saveBlackList(it)
                    }
                    load()
                }
            }
        }
        dialogFragment.show(parentFragmentManager, BlacklistDialogFragment::class.java.name)
    }

    companion object {
        val TAG = BlackListSettingFragment::class.java.simpleName

        fun newInstance(): BlackListSettingFragment {
            return BlackListSettingFragment()
        }
    }
}
