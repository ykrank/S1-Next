package me.ykrank.s1next.view.page.test

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.ykrank.androidtools.util.FileUtil
import me.ykrank.s1next.R
import me.ykrank.s1next.view.activity.GalleryActivity
import java.io.File
import java.util.Date
import java.util.Locale

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
        val file = list[position]
        Glide.with(holder.image)
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.image)

        holder.size.text = FileUtil.getPrintSize(file.length())
        holder.time.text =
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date(file.lastModified()))

        holder.image.setOnClickListener {
            GalleryActivity.startUri(it.context, file.toUri())
        }
    }

    fun updateData(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class ImageCacheViewVH(itemView: View) : ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.image)
    val size: TextView = itemView.findViewById(R.id.size)
    val time: TextView = itemView.findViewById(R.id.time)
}