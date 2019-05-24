package me.ykrank.s1next.view.adapter


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.common.base.Objects
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleViewHolderAdapter
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.R

class SimpleSpinnerAdapter<T>(context: Context, objects: List<T>, private val getName: (T?) -> String) : SimpleViewHolderAdapter<T, SimpleSpinnerAdapter.SimpleSpinnerViewHolder>(context, R.layout.spinner_simple, objects) {

    override fun onCreateViewHolder(parent: ViewGroup): SimpleSpinnerViewHolder {
        val root = layoutInflater.inflate(mResource, parent, false)
        return SimpleSpinnerViewHolder(root)
    }

    override fun onBindViewHolder(viewHolder: SimpleSpinnerViewHolder, data: T?, position: Int) {
        try {
            viewHolder.textView.text = getName.invoke(data)
        } catch (e: Exception) {
            L.report(e)
        }

    }


    override fun getItemId(position: Int): Long {
        return Objects.hashCode(getItem(position)).toLong()
    }

    class SimpleSpinnerViewHolder(rootView: View) : SimpleViewHolderAdapter.BaseViewHolder(rootView) {
        val textView: TextView = rootView as TextView
    }
}
