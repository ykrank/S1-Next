package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.dbmodel.ReadProgress;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.fragment.setting.ReadProgressPreferenceFragment;

/**
 * A helper class retrieving the download preferences from {@link SharedPreferences}.
 */
public final class ReadProgressPreferencesRepository extends BasePreferencesRepository {
    public static final String PREF_KEY_LAST_READ_PROGRESS = "pref_key_last_read_progress";
    
    @NonNull
    private ObjectMapper objectMapper;

    public ReadProgressPreferencesRepository(Context context, SharedPreferences sharedPreferences, @NonNull ObjectMapper objectMapper) {
        super(context, sharedPreferences);
        this.objectMapper = objectMapper;
    }
    
    public boolean isSaveAuto(){
        return mSharedPreferences.getBoolean(ReadProgressPreferenceFragment.PREF_KEY_READ_PROGRESS_SAVE_AUTO,
                mContext.getResources().getBoolean(R.bool.pref_read_progress_save_auto_default_value));
    }

    public boolean isLoadAuto(){
        return mSharedPreferences.getBoolean(ReadProgressPreferenceFragment.PREF_KEY_READ_PROGRESS_LOAD_AUTO,
                mContext.getResources().getBoolean(R.bool.pref_read_progress_load_auto_default_value));
    }
    
    @Nullable
    public ReadProgress getLastReadProgress(){
        String lastStr = mSharedPreferences.getString(PREF_KEY_LAST_READ_PROGRESS, null);
        try {
            return objectMapper.readValue(lastStr, ReadProgress.class);
        } catch (IOException e) {
            L.e(e);
            return null;
        }
    }
}
