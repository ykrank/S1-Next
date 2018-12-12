package me.ykrank.s1next.view.adapter

import android.content.Context
import com.github.ykrank.androidtools.ui.adapter.LibBaseRecyclerViewAdapter

abstract class BaseRecyclerViewAdapter : LibBaseRecyclerViewAdapter {

    constructor(context: Context) : this(context, true)

    constructor(context: Context, stableId: Boolean) : super(context, stableId)
}
