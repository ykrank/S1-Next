package com.github.ykrank.androidtools.widget.uploadimg

import android.view.View
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.databinding.ItemUploadedImageAddBinding
import com.github.ykrank.androidtools.databinding.ItemUploadedImageBinding
import com.github.ykrank.androidtools.ui.adapter.LibBaseRecyclerViewAdapter
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleAdapterDelegate

class ImageUploadAdapter(fragment: LibImageUploadFragment, imageClickListener: ((View, ModelImageUpload) -> Unit)? = null) : LibBaseRecyclerViewAdapter(fragment.context!!) {

    init {
        val context = fragment.context!!
        addAdapterDelegate(SimpleAdapterDelegate(context, R.layout.item_uploaded_image, ModelImageUpload::class.java,
                createViewHolderCallback = {
                    val binding = it as ItemUploadedImageBinding
                    binding.ivClose.setOnClickListener {
                        fragment.delPickedImage(binding.model)
                    }
                    if (imageClickListener != null) {
                        binding.image.setOnClickListener { view ->
                            binding.model?.also {
                                imageClickListener.invoke(view, it)
                            }
                        }
                    }
                }))
        addAdapterDelegate(SimpleAdapterDelegate(context, R.layout.item_uploaded_image_add, ModelImageUploadAdd::class.java,
                createViewHolderCallback = {
                    val binding = it as ItemUploadedImageAddBinding
                    binding.ivCorners.setOnClickListener {
                        fragment.startPickImage()
                    }
                }))
    }
}