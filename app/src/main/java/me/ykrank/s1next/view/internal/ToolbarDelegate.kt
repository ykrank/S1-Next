package me.ykrank.s1next.view.internal

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.ClipboardUtil
import com.github.ykrank.androidtools.util.ResourceUtil
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.PopWindowTitleBinding

/**
 * This class represents a delegate which you can use to add
 * [Toolbar] to [AppCompatActivity].
 */
class ToolbarDelegate(private val mAppCompatActivity: AppCompatActivity, val toolbar: Toolbar) {
    private var longTitleView: TextView? = null

    init {
        setUpToolbar()
    }

    /**
     * Sets a [Toolbar][android.widget.Toolbar] to act as the [android.support.v7.app.ActionBar]
     * for this Activity window.
     * Also displays home as an "up" affordance in Toolbar.
     */
    private fun setUpToolbar() {
        // designate a Toolbar as the ActionBar
        mAppCompatActivity.setSupportActionBar(toolbar)
        mAppCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //self defined title TextView
        longTitleView = toolbar.findViewById(R.id.toolbar_title_long)
        longTitleView?.setOnLongClickListener { v -> onLongClick(v, longTitleView?.text.toString()) }
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    fun setupNavCrossIcon() {
        toolbar.setNavigationIcon(ResourceUtil.getResourceId(mAppCompatActivity.theme,
                R.attr.iconClose))
    }

    /**
     * Set title
     *
     * @return whether handle this action
     */
    fun setTitle(title: CharSequence?): Boolean {
        longTitleView?.apply {
            if (!toolbar.title.isNullOrEmpty()) {
                toolbar.title = null
            }
            if (!title.isNullOrEmpty() && this.text != title) {
                this.text = title
            }
            return true
        }
        return false
    }

    private fun onLongClick(anchor: View, title: String): Boolean {
        val binding = PopWindowTitleBinding.inflate(LayoutInflater.from(anchor.context))
        binding.title = title
        binding.root.setOnClickListener { v ->
            ClipboardUtil.copyText(v.context, title, title)
            v.context.toast(R.string.title_copied)
        }

        val popupWindow = PopupWindow(binding.root,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = false
        popupWindow.showAsDropDown(anchor)
        return true
    }
}
