package me.ykrank.s1next.view.page.setting.fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import androidx.databinding.DataBindingUtil
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
import me.ykrank.s1next.data.db.biz.BlackWordBiz
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.databinding.FragmentBlackWordBinding
import me.ykrank.s1next.view.activity.DarkRoomActivity
import me.ykrank.s1next.view.adapter.BlackWordCursorListViewAdapter
import me.ykrank.s1next.view.dialog.BlackWordDialogFragment
import me.ykrank.s1next.view.fragment.BaseFragment
import me.ykrank.s1next.view.internal.RequestCode
import java.util.*

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
                    add()
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
                    val dialogFragment1 = BlackWordDialogFragment.newInstance(blackWord)
                    dialogFragment1.setTargetFragment(
                        this@BlackWordSettingFragment,
                        RequestCode.REQUEST_CODE_BLACKLIST
                    )
                    dialogFragment1.show(
                        fragmentManager!!,
                        BlackWordSettingFragment::class.java.name
                    )
                    return true
                }

                R.id.menu_delete -> {
                    val blackWords = ArrayList<BlackWord>()
                    for (i in 0 until checklist.size()) {
                        if (checklist.valueAt(i)) {
                            blackWords.add(mListViewAdapter.getItem(checklist.keyAt(i)))
                        }
                    }
                    BlackWordBiz.instance.delBlackWords(blackWords)
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

        mListViewAdapter = BlackWordCursorListViewAdapter(activity!!)
        mListView.adapter = mListViewAdapter
        mListView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
        mListView.setMultiChoiceModeListener(mActionModeCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_black_word, menu)
        menu.findItem(R.id.menu_refresh)?.isEnabled = true
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
                val blackWord =
                    data?.getParcelableExtra<BlackWord>(BlackWordDialogFragment.TAG_BLACK_WORD)
                if (blackWord != null) {
                    BlackWordBiz.instance.saveBlackWord(blackWord)
                    load()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun add() {
        val dialogFragment = BlackWordDialogFragment.newInstance(null)
        dialogFragment.setTargetFragment(
            this@BlackWordSettingFragment,
            RequestCode.REQUEST_CODE_BLACKLIST
        )
        dialogFragment.show(fragmentManager!!, BlackWordSettingFragment::class.java.name)
    }

    companion object {
        val TAG = BlackWordSettingFragment::class.java.name

        fun newInstance(): BlackWordSettingFragment {
            return BlackWordSettingFragment()
        }
    }
}
