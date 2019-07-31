package me.ykrank.s1next.view.adapter

import android.app.Activity
import android.content.Context
import android.database.Cursor
import androidx.databinding.DataBindingUtil
import androidx.cursoradapter.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.BlackListDbWrapper
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.ItemBlacklistBinding
import me.ykrank.s1next.viewmodel.BlackListViewModel


class BlackListCursorListViewAdapter(activity: Activity) : androidx.cursoradapter.widget.CursorAdapter(activity, null, true) {
    private val mLayoutInflater: LayoutInflater = activity.layoutInflater

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val itemBlacklistBinding = DataBindingUtil.inflate<ItemBlacklistBinding>(mLayoutInflater,
                R.layout.item_blacklist, parent, false)
        itemBlacklistBinding.blackListViewModel = BlackListViewModel()
        return itemBlacklistBinding.root
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val binding = DataBindingUtil.findBinding<ItemBlacklistBinding>(view)
        binding?.blackListViewModel?.blacklist?.set(BlackListDbWrapper.getInstance().fromBlackListCursor(cursor))
    }

    override fun getItem(position: Int): BlackList {
        val cursor = super.getItem(position) as Cursor
        return BlackListDbWrapper.getInstance().fromBlackListCursor(cursor)
    }
}
