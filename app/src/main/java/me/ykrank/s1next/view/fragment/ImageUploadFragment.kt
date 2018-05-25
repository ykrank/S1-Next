package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.View
import com.github.ykrank.androidtools.widget.RxBus
import com.github.ykrank.androidtools.widget.uploadimg.LibImageUploadFragment
import com.github.ykrank.androidtools.widget.uploadimg.ModelImageUpload
import me.ykrank.s1next.App
import me.ykrank.s1next.view.event.PostAddImageEvent
import javax.inject.Inject

class ImageUploadFragment : LibImageUploadFragment() {

    @Inject
    internal lateinit var mRxBus: RxBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override val imageClickListener: ((View, ModelImageUpload) -> Unit)? =
            { view, model -> model.url?.also { mRxBus.post(PostAddImageEvent(it)) } }

    companion object {
        val TAG: String = ImageUploadFragment::class.java.name

        fun newInstance(): ImageUploadFragment {
            return ImageUploadFragment()
        }
    }
}