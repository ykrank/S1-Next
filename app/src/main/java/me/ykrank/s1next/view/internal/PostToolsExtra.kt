package me.ykrank.s1next.view.internal

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.widget.EditText
import me.ykrank.s1next.R

abstract class PostToolsExtra(@DrawableRes val icon: Int, @StringRes val name: Int) {

    abstract fun onClick(editText: EditText)
}

class PostToolsExtraBold : PostToolsExtra(R.drawable.ic_bold, R.string.bold) {

    override fun onClick(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val selectText = editText.text.substring(start, end)
        editText.text.replace(start, end, "[b]$selectText[/b]")
    }

}

class PostToolsExtraItalic : PostToolsExtra(R.drawable.ic_italic, R.string.italic) {

    override fun onClick(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val selectText = editText.text.substring(start, end)
        editText.text.replace(start, end, "[i]$selectText[/i]")
    }

}

class PostToolsExtraUnderline : PostToolsExtra(R.drawable.ic_underline, R.string.underline) {

    override fun onClick(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val selectText = editText.text.substring(start, end)
        editText.text.replace(start, end, "[u]$selectText[/u]")
    }

}

class PostToolsExtraLink : PostToolsExtra(R.drawable.ic_link, R.string.link) {

    override fun onClick(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val selectText = editText.text.substring(start, end)

        editText.text.replace(start, end, "[url=]$selectText[/url]")
    }

}

class PostToolsExtraStrikethrough : PostToolsExtra(R.drawable.ic_strikethrough, R.string.strike_through) {

    override fun onClick(editText: EditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        val selectText = editText.text.substring(start, end)

        editText.text.replace(start, end, "[s]$selectText[/s]")
    }

}