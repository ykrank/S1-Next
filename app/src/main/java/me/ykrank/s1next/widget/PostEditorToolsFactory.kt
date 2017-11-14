package me.ykrank.s1next.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import me.ykrank.s1next.databinding.LayoutPostEditorToolsBinding

/**
 * Created by ykrank on 2017/11/14.
 */
class PostEditorToolsFactory(var editText: EditText?) {
    private var binding: LayoutPostEditorToolsBinding? = null

    fun createView(inflater: LayoutInflater, container: ViewGroup): View? {
        val binding = LayoutPostEditorToolsBinding.inflate(inflater, container, false)


        this.binding = binding
        return binding.root
    }

    fun destroy() {
        editText = null
    }
}