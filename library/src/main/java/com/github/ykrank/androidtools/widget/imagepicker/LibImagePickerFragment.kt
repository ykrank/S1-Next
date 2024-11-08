package com.github.ykrank.androidtools.widget.imagepicker

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.ui.LibBaseFragment
import com.github.ykrank.androidtools.util.FileUtil
import com.github.ykrank.androidtools.util.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


abstract class LibImagePickerFragment : LibBaseFragment() {

    private val imagePicker = ImagePicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker.initPicker(this, 5) { uris ->
            lifecycleScope.launch {
                val medias = withContext(Dispatchers.IO) {
                    val context = requireContext()
                    uris.map {
                        try {
                            val cacheFileName = FileUtil.createRandomFileName(
                                context,
                                ".webp"
                            )
                            val cacheFile = File(context.cacheDir, cacheFileName)
                            ImageCompress.compressImage(
                                context,
                                it,
                                FileOutputStream(cacheFile)
                            )
                            LocalMedia(it, isCompressed = true, compressPath = cacheFile.toUri())
                        } catch (e: Exception) {
                            L.report(e)
                            LocalMedia(it)
                        }
                    }
                }
                afterPickImage(medias)
            }
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