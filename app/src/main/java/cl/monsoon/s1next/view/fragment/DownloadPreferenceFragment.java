package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.DownloadPreferencesRepository;

public final class DownloadPreferenceFragment extends BasePreferenceFragment {

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        App.getAppComponent(activity).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.download_preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case DownloadPreferencesRepository.PREF_KEY_DOWNLOAD_AVATARS_STRATEGY:
                mDownloadPreferencesManager.invalidateAvatarsDownloadStrategy();

                break;
            case DownloadPreferencesRepository.PREF_KEY_AVATAR_RESOLUTION_STRATEGY:
                mDownloadPreferencesManager.invalidateAvatarsResolutionStrategy();

                break;
            case DownloadPreferencesRepository.PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL:
                mDownloadPreferencesManager.invalidateAvatarsCacheInvalidationIntervalStrategy();

                break;
            case DownloadPreferencesRepository.PREF_KEY_DOWNLOAD_IMAGES_STRATEGY:
                mDownloadPreferencesManager.invalidateImagesDownloadStrategy();

                break;
        }
    }
}
