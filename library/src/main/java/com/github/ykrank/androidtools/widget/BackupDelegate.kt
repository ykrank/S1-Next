package com.github.ykrank.androidtools.widget

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.content.Intent
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.*
import java.io.File
import java.io.IOException


/**
 * Created by AdminYkrank on 2016/4/21.
 * 设置数据库进行备份的代理
 */
class BackupDelegate(private val mContext: Context, private val backupFileName: String, private val dbName: String,
                     private val afterBackup: AfterBackup = DefaultAfterBackup(mContext),
                     private val afterRestore: AfterRestore = DefaultAfterRestore(mContext)) {

    fun backup(fragment: Fragment) {
        val intent = FilePickerUtil.dirPickIntent(mContext)
        fragment.startActivityForResult(intent, BACKUP_FILE_CODE)
    }

    fun restore(fragment: Fragment) {
        val intent = FilePickerUtil.filePickIntent(mContext)
        fragment.startActivityForResult(intent, RESTORE_FILE_CODE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (data == null){
            return false
        }
        if (requestCode == BACKUP_FILE_CODE) {
            FilePickerUtil.onFilePickResult(resultCode, data, object : FilePickerUtil.OnFilePickCallback {
                override fun success(file: File) {
                    RxJavaUtil.workWithUiResult({ doBackup(file) }, afterBackup::accept, this::error)
                }

                override fun cancel() {
                    afterBackup.accept(CANCELED)
                }

                override fun error(e: Throwable) {
                    L.e("BackupSetting:", e)
                }
            })
            return true
        } else if (requestCode == RESTORE_FILE_CODE) {
            FilePickerUtil.onFilePickResult(resultCode, data, object : FilePickerUtil.OnFilePickCallback {
                override fun success(file: File) {
                    RxJavaUtil.workWithUiResult({ doRestore(file) }, afterRestore::accept, this::error)
                }

                override fun cancel() {
                    afterRestore.accept(CANCELED)
                }

                override fun error(e: Throwable) {
                    L.e("RestoreSetting:", e)
                }
            })
            return true
        } else {
            return false
        }
    }

    @WorkerThread
    @BackupResult
    private fun doBackup(destDir: File): Int {
        LooperUtil.enforceOnWorkThread()
        try {
            var dirPath = destDir.path
            if (destDir.isDirectory) {
                if (!dirPath.endsWith("/")) {
                    dirPath += "/"
                }
                val dbFile = mContext.getDatabasePath(dbName)
                val destFile = File(dirPath + backupFileName)
                if (!destFile.exists()) {
                    destFile.createNewFile()
                }
                FileUtil.copyFile(dbFile, destFile)
                return SUCCESS
            } else
                return IO_EXCEPTION
        } catch (e: IOException) {
            L.e("BackupError:", e)
            return if (e.message?.contains("Permission denied") == true) {
                PERMISSION_DENY
            } else
                IO_EXCEPTION
        } catch (e: Exception) {
            L.e("BackupError:", e)
            return UNKNOWN_EXCEPTION
        }

    }

    @WorkerThread
    @BackupResult
    private fun doRestore(srcFile: File): Int {
        LooperUtil.enforceOnWorkThread()
        try {
            val filePath = srcFile.path
            if (srcFile.isFile) {
                if (SQLiteUtil.isValidSQLite(filePath)) {
                    val dbFile = mContext.getDatabasePath(dbName)
                    FileUtil.copyFile(srcFile, dbFile)
                    return SUCCESS
                }
            }
            return NO_DATA
        } catch (e: IOException) {
            L.e("RestoreError:", e)
            return if (e.message?.contains("Permission denied") == true) {
                PERMISSION_DENY
            } else
                IO_EXCEPTION
        } catch (e: Exception) {
            L.e("RestoreError:", e)
            return UNKNOWN_EXCEPTION
        }

    }

    @IntDef(SUCCESS, CANCELED, NO_DATA, PERMISSION_DENY, IO_EXCEPTION, UNKNOWN_EXCEPTION)
    annotation class BackupResult

    interface AfterBackup {
        fun accept(@BackupResult integer: Int?)
    }

    interface AfterRestore {
        fun accept(@BackupResult integer: Int?)
    }

    open class DefaultAfterBackup(val context: Context) : AfterBackup {

        @SuppressLint("SwitchIntDef")
        override fun accept(result: Int?) {
            @StringRes val message: Int =
                    when (result) {
                        BackupDelegate.SUCCESS -> R.string.message_backup_success
                        BackupDelegate.NO_DATA -> R.string.message_no_setting_data
                        BackupDelegate.PERMISSION_DENY -> R.string.message_permission_denied
                        BackupDelegate.IO_EXCEPTION -> R.string.message_io_exception
                        BackupDelegate.CANCELED -> R.string.message_operation_canceled
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
            @StringRes val message: Int =
                    when (result) {
                        BackupDelegate.SUCCESS -> R.string.message_restore_success
                        BackupDelegate.NO_DATA -> R.string.message_no_setting_data
                        BackupDelegate.PERMISSION_DENY -> R.string.message_permission_denied
                        BackupDelegate.IO_EXCEPTION -> R.string.message_io_exception
                        BackupDelegate.CANCELED -> R.string.message_operation_canceled
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

        private val BACKUP_FILE_CODE = 11
        private val RESTORE_FILE_CODE = 12
    }
}
