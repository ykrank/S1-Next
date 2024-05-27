package com.github.ykrank.androidtools.widget.imagepicker

import android.os.Bundle
import com.github.ykrank.androidtools.ui.LibBaseFragment


abstract class LibImagePickerFragment : LibBaseFragment() {

    private val imagePicker = ImagePicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker.initPicker(this, 5) { uris ->
            afterPickImage(uris.map { LocalMedia(it) })
        }
    }

    open fun startPickImage() {
        imagePicker.pickImage()
    }
    override fun onDestroy() {
        imagePicker.destroy()
        super.onDestroy()
    }

    abstract fun afterPickImage(medias: List<LocalMedia>)
}