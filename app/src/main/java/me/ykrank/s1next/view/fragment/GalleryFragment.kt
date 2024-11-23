package me.ykrank.s1next.view.fragment

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import com.github.ykrank.androidtools.util.ClipboardUtil
import com.github.ykrank.androidtools.util.FileUtil
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.isNetwork
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.databinding.FragmentGalleryBinding
import me.ykrank.s1next.databinding.MenuGalleryLargeImageSwitchBinding
import me.ykrank.s1next.util.AppFileUtil
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.viewmodel.ImageViewModel
import me.ykrank.s1next.widget.download.DownloadProgressModel
import me.ykrank.s1next.widget.download.DownloadTask
import me.ykrank.s1next.widget.download.ProgressListener
import me.ykrank.s1next.widget.download.ProgressManager
import me.ykrank.s1next.widget.image.ImageBiz
import me.ykrank.s1next.widget.image.image
import me.ykrank.s1next.widget.track.event.LargeImageTrackEvent
import me.ykrank.s1next.widget.track.event.ViewImageTrackEvent
import java.io.File
import javax.inject.Inject

/**
 * Created by ykrank on 2017/6/16.
 */
class GalleryFragment : Fragment() {
    private var mImageUrl: Uri? = null
    private var mImageThumbUrl: Uri? = null

    private var downloadId: String? = null

    private lateinit var mPhotoView: PhotoView
    private lateinit var binding: FragmentGalleryBinding

    private var largeModeBinding: MenuGalleryLargeImageSwitchBinding? = null
    private var largeModeMenu: MenuItem? = null
    private var large = false

    private var mProgressListener: ProgressListener? = null

    private val imageBiz by lazy {
        ImageBiz(mDownloadPrefManager)
    }

    @Inject
    internal lateinit var trackAgent: DataTrackAgent

    @Inject
    internal lateinit var mDownloadPrefManager: DownloadPreferencesManager

    @Inject
    internal lateinit var mProgressManager: ProgressManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        mPhotoView = binding.photoView
        mImageUrl = arguments?.getParcelable(ARG_IMAGE_URL)
        mImageThumbUrl = arguments?.getParcelable(ARG_IMAGE_THUMB_URL)

        L.leaveMsg("GalleryFragment##url:$mImageUrl,thumb:$mImageThumbUrl")

        trackAgent.post(ViewImageTrackEvent(mImageUrl?.toString(), mImageThumbUrl != null))

        preload()

        binding.downloadPrefManager = mDownloadPrefManager
        binding.imageViewModel = ImageViewModel(mImageUrl, mImageThumbUrl)

        mPhotoView.attacher.scaleType = ImageView.ScaleType.CENTER_INSIDE

        addProgressListener()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_gallery, menu)
        largeModeMenu = menu.findItem(R.id.menu_large_image_mode)
        largeModeMenu?.isChecked = large
        val largeModeMenu = largeModeMenu?.actionView
        if (largeModeMenu != null) {
            largeModeBinding = MenuGalleryLargeImageSwitchBinding.bind(largeModeMenu)
            largeModeBinding?.check = large
            largeModeBinding?.switchLarge?.setOnCheckedChangeListener { buttonView, isChecked ->
                switchLargeImage(isChecked)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_download -> {
                downloadImage()
                return true
            }

            R.id.menu_large_image_mode -> {
                switchLargeImage(!item.isChecked)
                return true
            }

            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(requireContext(), mImageUrl)
                return true
            }

            R.id.menu_copy_link -> {
                ClipboardUtil.copyText(requireContext(), "Url of image", mImageUrl.toString())
                Toast.makeText(requireContext(), R.string.message_link_copied, Toast.LENGTH_SHORT).show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun preload() {
        val uri = mImageUrl ?: return
        if (uri.isNetwork()) {
            Glide.with(App.get())
                .image(imageBiz, uri.toString(), forcePass = true)
                .preload()
        }
    }

    private fun switchLargeImage(large: Boolean) {
        if (this.large == large) {
            return
        }
        this.large = large
        largeModeMenu?.isChecked = large
        largeModeBinding?.check = large

        binding.large = large
        if (large) {
            mImageUrl?.let {
                trackAgent.post(LargeImageTrackEvent(it.toString(), mImageThumbUrl?.toString()))
            }
        }
    }

    private fun downloadImage() {
        val builder: RequestBuilder<File> = Glide.with(this)
            .downloadOnly()
            .image(imageBiz, mImageUrl, forcePass = true)

        builder.into(object : CustomTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                try {
                    val context = context ?: throw IllegalStateException("Context is null")
                    val type = FileUtil.getImageType(context, resource)
                    var imageType = FileUtil.getImageTypeSuffix(type)
                    if (imageType == null) {
                        imageType = ".jpg"
                    }
                    val name: String = AppFileUtil.createRandomFileName(context, imageType)
                    AppFileUtil.getDownloadPath(parentFragmentManager, { uri ->
                        val file = uri.createFile("image/${imageType}", name)
                        file?.uri?.also { fileUri ->
                            lifecycleScope.launch(
                                CoroutineExceptionHandler { _, e ->
                                    L.e(e)
                                    Toast.makeText(
                                        context,
                                        R.string.download_unknown_error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                withContext(Dispatchers.IO) {
                                    FileUtil.copyFile(
                                        resource,
                                        requireContext().contentResolver.openOutputStream(
                                            fileUri
                                        )!!
                                    )
                                }
                                Snackbar.make(
                                    binding.root,
                                    R.string.download_success,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                context.let { FileUtil.notifyImageInMediaStore(it, fileUri) }
                            }
                        }
                    })
                } catch (e: Exception) {
                    L.report(e)
                    Toast.makeText(context, R.string.download_unknown_error, Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }

    private fun addProgressListener() {
        //Avoid leak memory
        downloadId = mImageUrl?.toString()?.let { String(it.toCharArray()) }
        val progressListener = object : ProgressListener {

            override fun onProgress(task: DownloadTask, progress: DownloadProgressModel) {
                binding.progress =
                    ProgressItem(progress.totalLength, progress.currentOffset, progress.done)
            }
        }
        downloadId?.also {
            mProgressListener?.apply {
                mProgressManager.removeListener(it, this)
            }
            mProgressManager.addListener(it, progressListener)
        }
        mProgressListener = progressListener
    }

    override fun onDestroy() {
        downloadId?.also {
            mProgressListener?.apply {
                mProgressManager.removeListener(it, this)
            }
        }
        downloadId = null
        super.onDestroy()
    }

    companion object {
        val TAG: String = GalleryFragment::class.java.simpleName

        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_IMAGE_THUMB_URL = "image_thumb_url"
        fun instance(imageUrl: Uri, thumbUrl: Uri? = null): GalleryFragment {
            val fragment = GalleryFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_IMAGE_URL, imageUrl)
            bundle.putParcelable(ARG_IMAGE_THUMB_URL, thumbUrl)
            fragment.arguments = bundle
            return fragment
        }
    }
}