package me.ykrank.s1next.view.internal

interface ToolbarDropDownInterface {
    interface OnItemSelectedListener {
        fun onToolbarDropDownItemSelected(position: Int)
    }

    interface Callback {
        fun setupToolbarDropDown(dropDownItemList: List<CharSequence>)
    }
}
