package me.ykrank.s1next.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Size;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.activity.OpenSourceLicenseDetailActivity;

public final class OpenSourceLicensesFragment extends PreferenceFragment {

    private static final String PREF_KEY_LIBRARIES = "pref_key_libraries";
    private static final String PREF_KEY_FILES = "pref_key_files";

    private static final String EXTRAS_LIBRARY_OR_FILE_OPEN_SOURCE_LICENSE_FILE_PATH = "libraries_or_files_open_source_license_file_path";

    private static final String ASSET_PATH_OPEN_SOURCE_LICENSES_LIBRARY = "text/license/library/";
    private static final String ASSET_PATH_OPEN_SOURCE_LICENSES_FILE = "text/license/file/";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_open_souce_licenses);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        setupLibrariesPreference(preferenceScreen);
        setupFilesPreference(preferenceScreen);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        OpenSourceLicenseDetailActivity.startOpenSourceLicenseDetailActivity(preference.getContext(),
                preference.getTitle().toString(), preference.peekExtras().getString(
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
     * Gets each library's name and its license's name.
     */
    private String[][] getLibrariesInfos() {
        return new String[][]{
                {"AdapterDelegates", "ADAPTER_DELEGATES"},
                {"Android Support Library", "ANDROID_SUPPORT"},
                {"Apache Commons Lang", "APACHE_LICENSE_2.0"},
                {"android-apt", "UNLICENSE"},
                {"Bugsnag Android", "BUGSNAG_ANDROID"},
                {"Dagger 2", "DAGGER_2"},
                {"Data Binding", "ANDROID_SUPPORT"},
                {"FindBugs-jsr305", "APACHE_LICENSE_2.0"},
                {"Glide", "GLIDE"},
                {"Gradle Retrolambda Plugin", "GRADLE_RETROLAMBDA_PLUGIN"},
                {"Gradle Versions Plugin", "APACHE_LICENSE_2.0"},
                {"Guava", "APACHE_LICENSE_2.0"},
                {"jackson-databind", "APACHE_LICENSE_2.0"},
                {"JSR-250 Common Annotations for the JavaTM Platform", "CDDL_1.0"},
                {"LeakCanary", "LEAKCANARY"},
                {"OkHttp", "APACHE_LICENSE_2.0"},
                {"Retrofit", "RETROFIT"},
                {"Retrolambda", "APACHE_LICENSE_2.0"},
                {"RxAndroid", "RX_ANDROID"},
                {"RxJava", "RX_JAVA"},
                {"ActiveAndroid", "ACTIVE_ANDROID"}
        };
    }

    /**
     * Gets each file's name and its license's name.
     */
    private String[][] getFilesInfo() {
        return new String[][]{
                {"BezelImageView.java", "BEZEL_IMAGE_VIEW"},
                {"CookieStoreImpl.java", "COOKIE_STORE_IMPL"},
                {"TagFragmentStatePagerAdapter.java", "FRAGMENT_STATE_PAGER_ADAPTER"},
                {"PhotoView.java", "PHOTO_VIEW"}
        };
    }
}
