package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.dbmodel.ReadProgress;
import me.ykrank.s1next.util.L;

/**
 * A helper class retrieving the download preferences from {@link SharedPreferences}.
 */
public final class ReadProgressPreferencesRepository extends BasePreferencesRepository {

    @NonNull
    private ObjectMapper objectMapper;

    public ReadProgressPreferencesRepository(Context context, SharedPreferences sharedPreferences, @NonNull ObjectMapper objectMapper) {
        super(context, sharedPreferences);
        this.objectMapper = objectMapper;
    }

    public boolean isSaveAuto() {
        return getPrefBoolean(R.string.pref_key_read_progress_save_auto,
                R.bool.pref_read_progress_save_auto_default_value);
    }

    public boolean isLoadAuto() {
        return getPrefBoolean(R.string.pref_key_read_progress_load_auto,
                R.bool.pref_read_progress_load_auto_default_value);
    }

    @Nullable
    public ReadProgress getLastReadProgress() {
        try {
            String lastStr = getPrefString(R.string.pref_key_last_read_progress, null);
            if (!TextUtils.isEmpty(lastStr)) {
                return objectMapper.readValue(lastStr, ReadProgress.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            L.report(e);
            return null;
        }
    }

    public boolean saveLastReadProgress(ReadProgress readProgress) {
        try {
            String lastStr = objectMapper.writeValueAsString(readProgress);
            putPrefString(R.string.pref_key_last_read_progress, lastStr);
            return true;
        } catch (JsonProcessingException e) {
            L.report(e);
            return false;
        }
    }
}
