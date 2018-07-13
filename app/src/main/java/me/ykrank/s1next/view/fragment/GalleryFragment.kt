package me.ykrank.s1next.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import com.github.ykrank.androidtools.util.FileUtil
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.glide.model.ForcePassUrl
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.databinding.FragmentGalleryBinding
import me.ykrank.s1next.util.AppFileUtil
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.viewmodel.ImageViewModel
import me.ykrank.s1next.widget.track.event.ViewImageTrackEvent
import okhttp3.HttpUrl
import java.io.File
import java.lang.Exception
import javax.inject.Inject

/**
 * Created by ykrank on 2017/6/16.
 */
class GalleryFragment : Fragment() {
    private var mImageUrl: String? = null
    private var mImageThumbUrl: String? = null

    private var downloadId: String? = null

    private lateinit var mPhotoView: PhotoView
    private lateinit var binding: FragmentGalleryBinding

    @Inject
    internal lateinit var trackAgent: DataTrackAgent
    @Inject
    internal lateinit var mDownloadPrefManager: DownloadPreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        mPhotoView = binding.photoView
        mImageUrl = arguments!!.getString(ARG_IMAGE_URL)
        mImageThumbUrl = arguments!!.getString(ARG_IMAGE_THUMB_URL)

        L.leaveMsg("GalleryActivity##url:$mImageUrl,thumb:$mImageThumbUrl")

        trackAgent.post(ViewImageTrackEvent(mImageUrl, mImageThumbUrl != null))
        binding.downloadPrefManager = mDownloadPrefManager
        binding.imageViewModel = ImageViewModel(mImageUrl, mImageThumbUrl)

        mPhotoView.attacher.scaleType = ImageView.ScaleType.CENTER_INSIDE

        addProgressListener()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_gallery, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_download -> {
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                    return true
                }
                downloadImage()
                return true
            }
            R.id.menu_large_image_mode -> {
                val checked = item.isChecked
                item.isChecked = !checked
                binding.large = !checked
                return true
            }
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(context, Uri.parse(mImageUrl))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    downloadImage()
                } catch (e: SecurityException) {
                    Toast.makeText(context, R.string.message_permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @RequiresPermission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun downloadImage() {
        var builder: RequestBuilder<File> = Glide.with(this)
                .download(ForcePassUrl(mImageUrl))
        //avatar signature
        if (Api.isAvatarUrl(mImageUrl)) {
            builder = builder.apply(RequestOptions()
                    .signature(mDownloadPrefManager.avatarCacheInvalidationIntervalSignature))
        }
        builder.into(object : SimpleTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                RxJavaUtil.workWithUiResult({
                    val context = context ?: throw IllegalStateException("Context is null")
                    var name: String? = null
                    val file: File
                    val downloadDir = FileUtil.getDownloadDirectory(context)
                    val url = HttpUrl.parse(mImageUrl)
                    if (url != null) {
                        val segments = url.encodedPathSegments()
                        if (segments.size > 0) {
                            name = segments[segments.size - 1]
                        }
                        //sometime url is php
                        if (name != null && name.endsWith(".php")) {
                            name = null
                        }
                    }

                    val type = FileUtil.getImageType(context, resource)
                    var imageType = FileUtil.getImageTypeSuffix(type)
                    if (imageType == null) {
                        imageType = ".jpg"
                    }

                    if (!TextUtils.isEmpty(name)) {
                        if (!name!!.endsWith(imageType)) {
                            name += imageType
                        }
                        file = File(downloadDir, name)
                    } else {
                        file = AppFileUtil.newFileInDirectory(downloadDir, imageType)
                    }
                    FileUtil.copyFile(resource, file)
                    file
                }, { f ->
                    Snackbar.make(binding.root, R.string.download_success, Snackbar.LENGTH_SHORT).show()
                    context?.let { FileUtil.notifyImageInMediaStore(it, f) }
                }) { e ->
                    L.report(e)
                    Toast.makeText(context, R.string.download_unknown_error, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun addProgressListener() {
        downloadId = mImageUrl?.let { String(it.toCharArray()) }
        downloadId?.also {
            ProgressManager.getInstance().addResponseListener(it, object : ProgressListener {
                override fun onProgress(progressInfo: ProgressInfo) {
                    binding.progress = ProgressItem(progressInfo.contentLength, progressInfo.currentbytes, progressInfo.isFinish)
                }

                override fun onError(id: Long, e: Exception?) {
                    binding.progress = ProgressItem(0, 0, true)
                    L.report(e)
                }

            })
        }
    }

    override fun onDestroy() {
        downloadId = null
        super.onDestroy()
    }

    companion object {
        val TAG: String = GalleryFragment::class.java.name

        private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0

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