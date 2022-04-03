package me.ykrank.s1next.view.fragment
import  com.github.ykrank.androidtools.widget.imagepicker.ImagePicker;
import android.os.Bundle
import android.view.View
import com.github.ykrank.androidtools.widget.RxBus
import com.github.ykrank.androidtools.widget.uploadimg.ImageUploadManager
import com.github.ykrank.androidtools.widget.uploadimg.LibImageUploadFragment
import com.github.ykrank.androidtools.widget.uploadimg.ModelImageUpload
import com.luck.picture.lib.config.PictureConfig
import me.ykrank.s1next.App
import me.ykrank.s1next.view.event.PostAddImageEvent
import me.ykrank.s1next.widget.net.Image
import me.ykrank.s1next.widget.uploadimg.RIPImageUploadManager
import okhttp3.OkHttpClient
import javax.inject.Inject

class ImageUploadFragment : LibImageUploadFragment() {

    @Inject
    internal lateinit var mRxBus: RxBus

    @Inject
    @Image
    internal lateinit var mOkHttpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override val imageClickListener: ((View, ModelImageUpload) -> Unit)? =
            { view, model -> model.url?.also { mRxBus.post(PostAddImageEvent(it)) } }

    override fun startPickImage() {
        ImagePicker.pickImage(this, PictureConfig.CHOOSE_REQUEST, 5, false)
    }

    override fun provideImageUploadManager(): ImageUploadManager {
        return RIPImageUploadManager(_okHttpClient = mOkHttpClient)
    }

    companion object {
        val TAG: String = ImageUploadFragment::class.java.name

        fun newInstance(): ImageUploadFragment {
            return ImageUploadFragment()
        }
    }
}

