package me.ykrank.s1next.view.page.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.ykrank.androidtools.util.FileUtil
import me.ykrank.s1next.R
import java.io.File

/**
 * Created by ykrank on 9/3/24
 */
class ImageCacheViewAdapter : RecyclerView.Adapter<ImageCacheViewVH>() {

    private var list: List<File> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCacheViewVH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_cache_view, parent, false)

        return ImageCacheViewVH(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ImageCacheViewVH, position: Int) {
        Glide.with(holder.image)
            .load(list[position])
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.image)

        holder.size.text = FileUtil.getPrintSize(list[position].length())
    }

    fun updateData(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class ImageCacheViewVH(itemView: View) : ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.image)
    val size: TextView = itemView.findViewById(R.id.size)
}