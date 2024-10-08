package com.github.ykrank.androidtools.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


/**
 * Created by AdminYkrank on 2016/4/21.
 * 设置数据库进行备份的代理
 */
class BackupDelegate(
    private val mContext: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val backupFileName: String,
    private val dbName: String,
    private val afterBackup: AfterBackup = DefaultAfterBackup(mContext),
    private val afterRestore: AfterRestore = DefaultAfterRestore(mContext)
) {

    fun backup(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, backupFileName)
        }
        fragment.startActivityForResult(intent, BACKUP_FILE_CODE)
    }

    fun restore(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        fragment.startActivityForResult(intent, RESTORE_FILE_CODE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (data == null) {
            return false
        }
        if (requestCode == BACKUP_FILE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data.data ?: return false

                lifecycleOwner.lifecycleScope.launch {
                    val result = async(Dispatchers.IO) {
                        doBackup(uri)
                    }
                    try {
                        afterBackup.accept(result.await())
                    } catch (e: Exception) {
                        error(e)
                    }
                }
                return true
            }
        } else if (requestCode == RESTORE_FILE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data.data ?: return false
                lifecycleOwner.lifecycleScope.launch {
                    val result = async(Dispatchers.IO) {
                        doRestore(uri)
                    }
                    try {
                        afterRestore.accept(result.await())
                    } catch (e: Exception) {
                        error(e)
                    }
                }
                return true
            }
        }
        return false
    }

    private fun error(e: Throwable) {
        L.e("RestoreSetting:", e)
    }

    @WorkerThread
    @BackupResult
    private fun doBackup(destUri: Uri): Int {
        LooperUtil.enforceOnWorkThread()
        try {
            val dbFile = mContext.getDatabasePath(dbName)
            if (!dbFile.exists()) {
                return NO_DATA
            }

            val contentResolver = mContext.contentResolver
            contentResolver.openOutputStream(destUri)?.use { outputStream ->
                copyFile(dbFile, outputStream)
            } ?: return IO_EXCEPTION

            return SUCCESS
        } catch (e: IOException) {
            L.e("BackupError:", e)
            return if (e.message?.contains("Permission denied") == true) {
                PERMISSION_DENY
            } else IO_EXCEPTION
        } catch (e: Exception) {
            L.e("BackupError:", e)
            return UNKNOWN_EXCEPTION
        }
    }

    @WorkerThread
    @BackupResult
    private fun doRestore(srcUri: Uri): Int {
        LooperUtil.enforceOnWorkThread()
        try {
            val contentResolver = mContext.contentResolver
            contentResolver.takePersistableUriPermission(
                srcUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            contentResolver.openInputStream(srcUri)?.use { inputStream ->
                val dbFile = mContext.getDatabasePath(dbName)
                copyFile(inputStream, dbFile)
            } ?: return IO_EXCEPTION

            return SUCCESS
        } catch (e: IOException) {
            L.e("RestoreError:", e)
            return if (e.message?.contains("Permission denied") == true) {
                PERMISSION_DENY
            } else IO_EXCEPTION
        } catch (e: Exception) {
            L.e("RestoreError:", e)
            return UNKNOWN_EXCEPTION
        }
    }

    @IntDef(SUCCESS, CANCELED, NO_DATA, PERMISSION_DENY, IO_EXCEPTION, UNKNOWN_EXCEPTION)
    annotation class BackupResult

    interface AfterBackup {
        fun accept(@BackupResult result: Int?)
    }

    interface AfterRestore {
        fun accept(@BackupResult result: Int?)
    }

    open class DefaultAfterBackup(val context: Context) : AfterBackup {

        @SuppressLint("SwitchIntDef")
        override fun accept(result: Int?) {
            @StringRes val message: Int = when (result) {
                SUCCESS -> R.string.message_backup_success
                NO_DATA -> R.string.message_no_setting_data
                PERMISSION_DENY -> R.string.message_permission_denied
                IO_EXCEPTION -> R.string.message_io_exception
                CANCELED -> R.string.message_operation_canceled
                else -> R.string.message_unknown_error
            }
            invokeMsg(message)
        }

        open fun invokeMsg(@StringRes message: Int) {
            context.toast(message)
        }
    }

    open class DefaultAfterRestore(val context: Context) : AfterRestore {

        @SuppressLint("SwitchIntDef")
        override fun accept(result: Int?) {
            @StringRes val message: Int = when (result) {
                SUCCESS -> R.string.message_restore_success
                NO_DATA -> R.string.message_no_setting_data
                PERMISSION_DENY -> R.string.message_permission_denied
                IO_EXCEPTION -> R.string.message_io_exception
                CANCELED -> R.string.message_operation_canceled
                else -> R.string.message_unknown_error
            }
            invokeMsg(message)
        }

        open fun invokeMsg(@StringRes message: Int) {
            context.toast(message)
        }
    }

    companion object {
        const val SUCCESS = 0
        const val NO_DATA = 1
        const val PERMISSION_DENY = 2
        const val IO_EXCEPTION = 3
        const val CANCELED = 4
        const val UNKNOWN_EXCEPTION = 99

        private const val BACKUP_FILE_CODE = 11
        private const val RESTORE_FILE_CODE = 12
    }

    private fun copyFile(source: File, sink: OutputStream) {
        source.inputStream().use { input ->
            sink.use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun copyFile(source: InputStream, sink: File) {
        source.use { input ->
            sink.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}

