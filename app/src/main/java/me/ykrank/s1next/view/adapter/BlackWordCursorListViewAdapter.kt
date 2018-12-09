package me.ykrank.s1next.view.adapter

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.BlackWordDbWrapper
import me.ykrank.s1next.data.db.dbmodel.BlackWord
import me.ykrank.s1next.databinding.ItemBlackwordBinding
import me.ykrank.s1next.viewmodel.BlackWordViewModel


class BlackWordCursorListViewAdapter(activity: Activity) : CursorAdapter(activity, null, true) {
    private val mLayoutInflater: LayoutInflater = activity.layoutInflater

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ItemBlackwordBinding>(mLayoutInflater,
                R.layout.item_blackword, parent, false)
        binding.model = BlackWordViewModel()
        return binding.root
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val binding = DataBindingUtil.findBinding<ItemBlackwordBinding>(view)
        binding?.model?.blackword?.set(BlackWordDbWrapper.instance.fromBlackWordCursor(cursor))
    }

    override fun getItem(position: Int): BlackWord {
        val cursor = super.getItem(position) as Cursor
        return BlackWordDbWrapper.instance.fromBlackWordCursor(cursor)
    }
}
