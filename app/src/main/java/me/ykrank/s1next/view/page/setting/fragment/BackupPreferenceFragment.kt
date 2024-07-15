package me.ykrank.s1next.view.page.setting.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.preference.Preference
import com.github.ykrank.androidtools.util.LooperUtil.enforceOnMainThread
import com.github.ykrank.androidtools.widget.BackupDelegate
import com.github.ykrank.androidtools.widget.BackupDelegate.AfterBackup
import com.github.ykrank.androidtools.widget.BackupDelegate.AfterRestore
import com.github.ykrank.androidtools.widget.BackupDelegate.BackupResult
import com.google.android.material.snackbar.Snackbar
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.AppDatabaseManager
import javax.inject.Inject

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
class BackupPreferenceFragment : BasePreferenceFragment(), Preference.OnPreferenceClickListener {
    private var backupAgent: BackupDelegate? = null

    @Inject
    lateinit var databaseManager: AppDatabaseManager

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        appComponent.inject(this)
        addPreferencesFromResource(R.xml.preference_backup)

        findPreference<Preference>(getString(R.string.pref_key_backup_backup))?.onPreferenceClickListener =
            this
        findPreference<Preference>(getString(R.string.pref_key_backup_restore))?.onPreferenceClickListener =
            this

        backupAgent = BackupDelegate(
            requireActivity(),
            this,
            BACKUP_FILE_NAME,
            BuildConfig.DB_NAME,
            object : AfterBackup {
                override fun accept(result: Int?) {
                    this@BackupPreferenceFragment.afterBackup(result)
                }
            },
            object : AfterRestore {
                override fun accept(result: Int?) {
                    this@BackupPreferenceFragment.afterRestore(result)
                }
            })
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val key = preference.key ?: return false
        if (key == getString(R.string.pref_key_backup_backup)) {
            backupAgent?.backup(this)
            return true
        } else if (key == getString(R.string.pref_key_backup_restore)) {
            databaseManager.close()
            backupAgent?.restore(this)
            return true
        }
        return false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (backupAgent?.onActivityResult(
                requestCode,
                resultCode,
                data
            ) != true
        ) super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    @MainThread
    private fun afterBackup(@BackupResult result: Int?) {
        enforceOnMainThread()
        @StringRes val message = when (result) {
            BackupDelegate.SUCCESS -> com.github.ykrank.androidtools.R.string.message_backup_success
            BackupDelegate.NO_DATA -> com.github.ykrank.androidtools.R.string.message_no_setting_data
            BackupDelegate.PERMISSION_DENY -> com.github.ykrank.androidtools.R.string.message_permission_denied
            BackupDelegate.IO_EXCEPTION -> com.github.ykrank.androidtools.R.string.message_io_exception
            BackupDelegate.CANCELED -> com.github.ykrank.androidtools.R.string.message_operation_canceled
            else -> com.github.ykrank.androidtools.R.string.message_unknown_error
        }
        view?.apply {
            Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
        }
    }


    @MainThread
    private fun afterRestore(@BackupResult result: Int?) {
        enforceOnMainThread()
        databaseManager.getOrBuildDb()
        @StringRes val message = when (result) {
            BackupDelegate.SUCCESS -> com.github.ykrank.androidtools.R.string.message_restore_success
            BackupDelegate.NO_DATA -> com.github.ykrank.androidtools.R.string.message_no_setting_data
            BackupDelegate.PERMISSION_DENY -> com.github.ykrank.androidtools.R.string.message_permission_denied
            BackupDelegate.IO_EXCEPTION -> com.github.ykrank.androidtools.R.string.message_io_exception
            BackupDelegate.CANCELED -> com.github.ykrank.androidtools.R.string.message_operation_canceled
            else -> com.github.ykrank.androidtools.R.string.message_unknown_error
        }
        view?.apply {
            Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        val TAG: String = BackupPreferenceFragment::class.java.name

        const val BACKUP_FILE_NAME: String = "S1Next_v" + BuildConfig.VERSION_CODE + ".bak"
    }
}
