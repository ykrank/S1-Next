package cl.monsoon.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.fragment.setting.DownloadPreferenceFragment;
import cl.monsoon.s1next.view.fragment.setting.GeneralPreferenceFragment;
import cl.monsoon.s1next.view.fragment.setting.ReadProgressPreferenceFragment;

/**
 * A helper class retrieving the download preferences from {@link SharedPreferences}.
 */
public final class ReadProgressPreferencesRepository extends BasePreferencesRepository {

    public ReadProgressPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        super(context, sharedPreferences);
    }
    
    public boolean isSaveAuto(){
        return mSharedPreferences.getBoolean(ReadProgressPreferenceFragment.PREF_KEY_READ_PROGRESS_SAVE_AUTO,
                mContext.getResources().getBoolean(R.bool.pref_read_progress_save_auto_default_value));
    }

    public boolean isLoadAuto(){
        return mSharedPreferences.getBoolean(ReadProgressPreferenceFragment.PREF_KEY_READ_PROGRESS_LOAD_AUTO,
                mContext.getResources().getBoolean(R.bool.pref_read_progress_load_auto_default_value));
    }
}
