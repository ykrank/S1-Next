package me.ykrank.s1next.view.fragment.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;

import com.activeandroid.ActiveAndroid;

import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.util.LooperUtil;
import me.ykrank.s1next.widget.BackupDelegate;
import me.ykrank.s1next.widget.BackupDelegate.BackupResult;

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
public final class BackupPreferenceFragment extends BasePreferenceFragment
        implements Preference.OnPreferenceClickListener {
    public static final String TAG = BackupPreferenceFragment.class.getName();

    public static final String BACKUP_FILE_NAME = "S1Next_v" + BuildConfig.VERSION_CODE + ".bak";

    public static final String PREF_KEY_BACKUP_BACKUP = "pref_key_backup_backup";
    public static final String PREF_KEY_BACKUP_RESTORE = "pref_key_backup_restore";

    private BackupDelegate backupAgent;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_backup);

        findPreference(PREF_KEY_BACKUP_BACKUP).setOnPreferenceClickListener(this);
        findPreference(PREF_KEY_BACKUP_RESTORE).setOnPreferenceClickListener(this);

        backupAgent = new BackupDelegate(getActivity(), this::afterBackup, this::afterRestore);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREF_KEY_BACKUP_BACKUP:
                backupAgent.backup(this);
                return true;
            case PREF_KEY_BACKUP_RESTORE:
                ActiveAndroid.dispose();
                backupAgent.restore(this);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!backupAgent.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @MainThread
    private void afterBackup(@BackupResult int result) {
        LooperUtil.enforceOnMainThread();
        @StringRes int message;
        switch (result) {
            case BackupDelegate.SUCCESS:
                message = R.string.message_backup_success;
                break;
            case BackupDelegate.NO_DATA:
                message = R.string.message_no_setting_data;
                break;
            case BackupDelegate.PERMISSION_DENY:
                message = R.string.message_permission_denied;
                break;
            case BackupDelegate.IO_EXCEPTION:
                message = R.string.message_io_exception;
                break;
            default:
                message = R.string.message_unknown_error;
        }
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }


    @MainThread
    private void afterRestore(@BackupResult int result) {
        LooperUtil.enforceOnMainThread();
        ActiveAndroid.initialize(getActivity().getApplicationContext());

        @StringRes int message;
        switch (result) {
            case BackupDelegate.SUCCESS:
                message = R.string.message_restore_success;
                break;
            case BackupDelegate.NO_DATA:
                message = R.string.message_no_setting_data;
                break;
            case BackupDelegate.PERMISSION_DENY:
                message = R.string.message_permission_denied;
                break;
            case BackupDelegate.IO_EXCEPTION:
                message = R.string.message_io_exception;
                break;
            default:
                message = R.string.message_unknown_error;
        }
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
