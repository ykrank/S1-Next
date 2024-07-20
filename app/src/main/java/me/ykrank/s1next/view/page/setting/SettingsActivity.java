package me.ykrank.s1next.view.page.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.ykrank.androidtools.util.L;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.page.setting.blacklist.BlackListSettingFragment;
import me.ykrank.s1next.view.page.setting.fragment.BackupPreferenceFragment;
import me.ykrank.s1next.view.page.setting.blacklist.BlackWordSettingFragment;
import me.ykrank.s1next.view.page.setting.fragment.DownloadPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.GeneralPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.NetworkPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.ReadPreferenceFragment;

/**
 * An Activity includes settings that allow users
 * to modify our app features and behaviors.
 */
public final class SettingsActivity extends BaseActivity {
    private static final String ARG_SHOW_SETTINGS = "show_settings";

    private static final int EXTRA_SHOW_SETTING_DEFAULT = 0;
    private static final int EXTRA_SHOW_SETTING_DOWNLOAD = 1;
    private static final int EXTRA_SHOW_SETTING_BLACKLIST = 2;
    private static final int EXTRA_SHOW_SETTING_READ_PROGRESS = 3;
    private static final int EXTRA_SHOW_SETTING_BACKUP = 4;
    private static final int EXTRA_SHOW_SETTING_NETWORK = 5;
    private static final int EXTRA_SHOW_SETTING_BLACK_WORD = 6;

    public static void startSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void startDownloadSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_DOWNLOAD);
        context.startActivity(intent);
    }

    public static void startBlackListSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_BLACKLIST);
        context.startActivity(intent);
    }

    public static void startBlackWordSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_BLACK_WORD);
        context.startActivity(intent);
    }

    public static void startReadProgressSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_READ_PROGRESS);
        context.startActivity(intent);
    }

    public static void startBackupSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_BACKUP);
        context.startActivity(intent);
    }

    public static void startNetworkSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_NETWORK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.leaveMsg("SettingsActivity##extra" + getIntent().getIntExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_DEFAULT));
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        if (savedInstanceState == null) {
            switch (getIntent().getIntExtra(ARG_SHOW_SETTINGS, EXTRA_SHOW_SETTING_DEFAULT)) {
                case EXTRA_SHOW_SETTING_DOWNLOAD:
                    setTitle(R.string.pref_downloads_and_cache);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            new DownloadPreferenceFragment()).commit();
                    break;
                case EXTRA_SHOW_SETTING_BLACKLIST:
                    setTitle(R.string.pref_blacklists);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            BlackListSettingFragment.Companion.newInstance(), BlackListSettingFragment.Companion.getTAG()).commit();
                    break;
                case EXTRA_SHOW_SETTING_READ_PROGRESS:
                    setTitle(R.string.pref_post_read);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            new ReadPreferenceFragment()).commit();
                    break;
                case EXTRA_SHOW_SETTING_BACKUP:
                    setTitle(R.string.pref_backup);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            new BackupPreferenceFragment()).commit();
                    break;
                case EXTRA_SHOW_SETTING_NETWORK:
                    setTitle(R.string.pref_network);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            new NetworkPreferenceFragment()).commit();
                    break;
                case EXTRA_SHOW_SETTING_BLACK_WORD:
                    setTitle(R.string.pref_black_words);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            BlackWordSettingFragment.Companion.newInstance(), BlackWordSettingFragment.Companion.getTAG()).commit();
                    break;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            new GeneralPreferenceFragment()).commit();
                    break;
            }
        }
    }
}
