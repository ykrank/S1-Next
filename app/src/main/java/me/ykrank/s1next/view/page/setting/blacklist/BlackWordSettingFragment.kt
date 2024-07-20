package me.ykrank.s1next.view.page.setting.blacklist

import android.database.Cursor
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
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.biz.BlackWordBiz
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.databinding.FragmentBlackWordBinding
import me.ykrank.s1next.view.activity.DarkRoomActivity
import me.ykrank.s1next.view.adapter.BlackWordCursorListViewAdapter
import me.ykrank.s1next.view.fragment.BaseFragment
import me.ykrank.s1next.view.internal.RequestCode

class BlackWordSettingFragment : BaseFragment() {

    private lateinit var mListView: ListView
    private lateinit var mListViewAdapter: BlackWordCursorListViewAdapter

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
                    showDialog(null)
                    return true
                }

                R.id.menu_edit -> {
                    var blackWord: BlackWord? = null
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackWord = mListViewAdapter.getItem(checklist.keyAt(i))
                            break
                        }
                    }
                    showDialog(blackWord)
                    return true
                }

                R.id.menu_delete -> {
                    val blackWords = ArrayList<BlackWord>()
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackWords.add(mListViewAdapter.getItem(checklist.keyAt(i)))
                        }
                    }
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){
                            BlackWordBiz.instance.delBlackWords(blackWords)
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

    internal val sourceObservable: Single<Cursor>
        get() = Single.fromCallable { BlackWordBiz.instance.blackWordCursor }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentBlackWordBinding>(
            inflater,
            R.layout.fragment_black_word, container, false
        )
        mListView = binding.listview
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListViewAdapter = BlackWordCursorListViewAdapter(requireActivity())
        mListView.adapter = mListViewAdapter
        mListView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
        mListView.setMultiChoiceModeListener(mActionModeCallback)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_black_word, menu)
        menu.findItem(R.id.menu_refresh)?.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                showDialog(null)
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

    private fun showDialog(nBlackWord: BlackWord?) {
        val dialogFragment = BlackWordDialogFragment.newInstance(nBlackWord)
        parentFragmentManager.setFragmentResultListener(
            RequestCode.REQUEST_KEY_BLACKLIST,
            this
        ) { _, result ->
            val blackWord: BlackWord? =
                result.getParcelable(BlackWordDialogFragment.TAG_BLACK_WORD)
            if (blackWord != null) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        BlackWordBiz.instance.saveBlackWord(blackWord)
                    }
                    load()
                }
            }
        }
        dialogFragment.show(parentFragmentManager, BlackWordDialogFragment::class.java.name)
    }

    companion object {
        val TAG = BlackWordSettingFragment::class.java.name

        fun newInstance(): BlackWordSettingFragment {
            return BlackWordSettingFragment()
        }
    }
}
