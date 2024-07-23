package me.ykrank.s1next.widget.saf

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract.EXTRA_INITIAL_URI
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.App
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import javax.inject.Inject


/**
 * Created by Cintory on 2024/6/7 16:11
 * Email：Cintory@gmail.com
 */
class SAFFragment : Fragment() {

    @Inject
    internal lateinit var mDownloadPreferencesManager: DownloadPreferencesManager

    private var getDictionaryCallback: ((DocumentFile) -> Unit)? = null

    private val setDownloadPathResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data.let { uri ->
                    uri?.let {
                        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        requireContext().contentResolver.takePersistableUriPermission(
                            uri,
                            takeFlags
                        )
                        val documentUri = DocumentFile.fromTreeUri(requireContext(), uri)
                        mDownloadPreferencesManager.downloadPath = uri.toString()
                        getDictionaryCallback?.invoke(documentUri!!)
                    }
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    fun getDownloadPath(callback: ((DocumentFile) -> Unit)?, focusResetPath: Boolean = false) {
        getDictionaryCallback = callback
        val downloadPath = mDownloadPreferencesManager.downloadPath
        if (focusResetPath || downloadPath.isNullOrEmpty()) {
            setDownloadPath()
            return
        }
        try {
            val documentFile = DocumentFile.fromTreeUri(
                requireContext(),
                Uri.parse(downloadPath)
            )
            if (documentFile != null) {
                callback?.invoke(documentFile)
                return
            }
        } catch (e: Exception) {
            L.report(e)
            setDownloadPath()
        }
    }

    private fun setDownloadPath() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            putExtra(EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        }
        setDownloadPathResultLauncher.launch(intent)
    }


    companion object {
        val TAG: String = SAFFragment::class.java.simpleName
    }
}