package cl.monsoon.s1next.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.activity.OpenSourceLicenseDetailActivity;

public final class OpenSourceLicensesFragment extends PreferenceFragment {

    private static final String PREF_KEY_LIBRARIES = "pref_key_libraries";
    private static final String PREF_KEY_FILES = "pref_key_files";

    private static final String EXTRAS_LIBRARY_OR_FILE_OPEN_SOURCE_LICENSE_FILE_PATH = "libraries_or_files_open_source_license_file_path";

    private static final String ASSET_PATH_OPEN_SOURCE_LICENSES_LIBRARY = "text/license/library/";
    private static final String ASSET_PATH_OPEN_SOURCE_LICENSES_FILE = "text/license/file/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_open_souce_licenses);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        setupLibrariesPreference(preferenceScreen);
        setupFilesPreference(preferenceScreen);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        OpenSourceLicenseDetailActivity.startOpenSourceLicenseDetailActivity(preference.getContext(),
                preference.getTitle(), preference.peekExtras().getString(
                        EXTRAS_LIBRARY_OR_FILE_OPEN_SOURCE_LICENSE_FILE_PATH));

        return true;
    }

    /**
     * Adds libraries to its PreferenceCategory programmatically.
     */
    private void setupLibrariesPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory)
                preferenceScreen.findPreference(PREF_KEY_LIBRARIES);
        Context context = preferenceCategory.getContext();
        for (@Size(2) String[] libraryInfos : getLibrariesInfos()) {
            Preference preference = new Preference(context);
            preference.setTitle(libraryInfos[0]);
            preference.setPersistent(false);
            // put this library's license path to extra
            preference.getExtras().putString(EXTRAS_LIBRARY_OR_FILE_OPEN_SOURCE_LICENSE_FILE_PATH,
                    ASSET_PATH_OPEN_SOURCE_LICENSES_LIBRARY + libraryInfos[1]);
            preferenceCategory.addPreference(preference);
        }
    }

    /**
     * Adds files to its PreferenceCategory programmatically.
     */
    private void setupFilesPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory)
                preferenceScreen.findPreference(PREF_KEY_FILES);
        Context context = preferenceCategory.getContext();
        for (@Size(2) String[] fileInfos : getFilesInfo()) {
            Preference preference = new Preference(context);
            preference.setTitle(fileInfos[0]);
            preference.setPersistent(false);
            // put this file's license path to extra
            preference.getExtras().putString(EXTRAS_LIBRARY_OR_FILE_OPEN_SOURCE_LICENSE_FILE_PATH,
                    ASSET_PATH_OPEN_SOURCE_LICENSES_FILE + fileInfos[1]);
            preferenceCategory.addPreference(preference);
        }
    }

    /**
     * Get each library's name and its license's name.
     */
    private String[][] getLibrariesInfos() {
        return new String[][]{
                {"Android Support Library", "ANDROID_SUPPORT"},
                {"Apache Commons Lang", "APACHE_LICENSE"},
                {"Bugsnag Android", "BUGSNAG_ANDROID"},
                {"Dagger 2", "DAGGER_2"},
                {"Data Binding", "ANDROID_SUPPORT"},
                {"FindBugs Jsr305", "GNU_LESSER_GPL"},
                {"Glide", "GLIDE"},
                {"Gradle Android Apt Plugin", "UNLICENSE"},
                {"Gradle Retrolambda Plugin", "GRADLE_RETROLAMBDA_PLUGIN"},
                {"Guava", "APACHE_LICENSE"},
                {"Jackson databind", "APACHE_LICENSE"},
                {"Javax.annotation API", "CDDL+GPL"},
                {"OkHttp", "APACHE_LICENSE"},
                {"Retrofit", "RETROFIT"},
                {"Retrolambda", "APACHE_LICENSE"},
                {"RxAndroid", "APACHE_LICENSE"},
                {"RxJava", "RXJAVA"}
        };
    }

    /**
     * Get each file's name and its license's name.
     */
    private String[][] getFilesInfo() {
        return new String[][]{
                {"BezelImageView.java", "BEZEL_IMAGE_VIEW"},
                {"CookieStoreImpl.java", "COOKIE_STORE_IMPL"},
                {"FragmentStatePagerAdapter.java", "FRAGMENT_STATE_PAGER_ADAPTER"},
                {"PhotoView.java", "PHOTO_VIEW"}
        };
    }
}
