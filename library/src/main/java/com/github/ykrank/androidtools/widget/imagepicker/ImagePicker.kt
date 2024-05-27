package com.github.ykrank.androidtools.widget.imagepicker

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ImagePicker {
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null

    fun initPicker(fragment: Fragment, maxSelect: Int, callback: (List<Uri>) -> Unit) {
        pickMedia = if (maxSelect == 1) {
            fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                callback(listOfNotNull(uri))
            }
        } else {
            fragment.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(
                    maxSelect
                )
            ) { uris ->
                callback(uris)
            }
        }
    }

    fun pickImage() {
        pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun destroy() {
        pickMedia?.unregister()
    }
}