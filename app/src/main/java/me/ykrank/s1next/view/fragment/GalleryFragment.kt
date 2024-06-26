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
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import com.github.ykrank.androidtools.util.FileUtil
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.glide.model.ForcePassUrl
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.google.android.material.snackbar.Snackbar
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.databinding.FragmentGalleryBinding
import me.ykrank.s1next.databinding.MenuGalleryLargeImageSwitchBinding
import me.ykrank.s1next.util.AppFileUtil
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.viewmodel.ImageViewModel
import me.ykrank.s1next.widget.download.ProgressListener
import me.ykrank.s1next.widget.download.ProgressManager
import me.ykrank.s1next.widget.track.event.LargeImageTrackEvent
import me.ykrank.s1next.widget.track.event.ViewImageTrackEvent
import java.io.File
import javax.inject.Inject

/**
 * Created by ykrank on 2017/6/16.
 */
class GalleryFragment : androidx.fragment.app.Fragment() {
    private var mImageUrl: String? = null
    private var mImageThumbUrl: String? = null

    private var downloadId: String? = null

    private lateinit var mPhotoView: PhotoView
    private lateinit var binding: FragmentGalleryBinding
    private var preloadTarget: Target<Drawable>? = null

    private var largeModeBinding: MenuGalleryLargeImageSwitchBinding? = null
    private var largeModeMenu: MenuItem? = null
    private var large = false

    @Inject
    internal lateinit var trackAgent: DataTrackAgent

    @Inject
    internal lateinit var mDownloadPrefManager: DownloadPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        mPhotoView = binding.photoView
        mImageUrl = arguments?.getString(ARG_IMAGE_URL)
        mImageThumbUrl = arguments?.getString(ARG_IMAGE_THUMB_URL)

        L.leaveMsg("GalleryFragment##url:$mImageUrl,thumb:$mImageThumbUrl")

        trackAgent.post(ViewImageTrackEvent(mImageUrl, mImageThumbUrl != null))

        preload()

        binding.downloadPrefManager = mDownloadPrefManager
        binding.imageViewModel = ImageViewModel(mImageUrl, mImageThumbUrl)

        mPhotoView.attacher.scaleType = ImageView.ScaleType.CENTER_INSIDE

        addProgressListener()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.fragment_gallery, menu)
        largeModeMenu = menu?.findItem(R.id.menu_large_image_mode)
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
                IntentUtil.startViewIntentExcludeOurApp(requireContext(), Uri.parse(mImageUrl))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun preload() {
        preloadTarget = Glide.with(App.get())
            .load(mImageUrl)
            .priority(Priority.HIGH)
            .preload()
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
                trackAgent.post(LargeImageTrackEvent(it, mImageThumbUrl))
            }
        }
    }

    private fun downloadImage() {
        var builder: RequestBuilder<File> = Glide.with(this)
            .download(ForcePassUrl(mImageUrl))
        //avatar signature
        if (Api.isAvatarUrl(mImageUrl)) {
            builder = builder.apply(
                RequestOptions()
                    .signature(mDownloadPrefManager.avatarCacheInvalidationIntervalSignature)
            )
        }
        builder.into(object : CustomTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                try {
                    val context = context ?: throw IllegalStateException("Context is null")
                    val type = FileUtil.getImageType(context, resource)
                    var imageType = FileUtil.getImageTypeSuffix(type)
                    if (imageType == null) {
                        imageType = ".jpg"
                    }
                    val name: String = AppFileUtil.createRandomFileName(imageType)
                    AppFileUtil.getDownloadPath(parentFragmentManager, { uri ->
                        val file = uri.createFile("image/${imageType}", name)
                        RxJavaUtil.workWithUiResult({
                            FileUtil.copyFile(
                                resource,
                                requireContext().contentResolver.openOutputStream(file?.uri!!)!!
                            )
                            return@workWithUiResult file.uri
                        }, { f ->
                            Snackbar.make(
                                binding.root,
                                R.string.download_success,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                            context.let { FileUtil.notifyImageInMediaStore(it, f) }
                        }) { e ->
                            L.report(e)
                            Toast.makeText(
                                context,
                                R.string.download_unknown_error,
                                Toast.LENGTH_SHORT
                            ).show()
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
        downloadId = mImageUrl?.let { String(it.toCharArray()) }
        downloadId?.also {
            ProgressManager.addListener(it, object : ProgressListener {
                override fun onProgress(
                    task: DownloadTask,
                    currentOffset: Long,
                    totalLength: Long
                ) {
                    binding.progress =
                        ProgressItem(totalLength, currentOffset, totalLength == currentOffset)
                }

                override fun taskEnd(
                    task: DownloadTask,
                    cause: EndCause,
                    realCause: java.lang.Exception?,
                    model: Listener1Assist.Listener1Model
                ) {
                    binding.progress = ProgressItem(model.totalLength, model.totalLength, true)
                    if (realCause != null) {
                        L.report(realCause)
                    }
                }

            })
        }
    }

    override fun onDestroy() {
        downloadId = null
        Glide.with(App.get()).clear(preloadTarget)
        super.onDestroy()
    }

    companion object {
        val TAG: String = GalleryFragment::class.java.name

        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_IMAGE_THUMB_URL = "image_thumb_url"
        fun instance(imageUrl: String, thumbUrl: String? = null): GalleryFragment {
            val fragment = GalleryFragment()
            val bundle = Bundle()
            bundle.putString(ARG_IMAGE_URL, imageUrl)
            bundle.putString(ARG_IMAGE_THUMB_URL, thumbUrl)
            fragment.arguments = bundle
            return fragment
        }
    }
}