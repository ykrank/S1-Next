package me.ykrank.s1next;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Component;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.Wifi;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.db.AppDaoSessionManager;
import me.ykrank.s1next.data.db.DbModule;
import me.ykrank.s1next.data.pref.NetworkPreferencesManager;
import me.ykrank.s1next.data.pref.PrefModule;
import me.ykrank.s1next.view.adapter.delegate.FavouriteAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmGroupsAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmLeftAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmRightAdapterDelegate;
import me.ykrank.s1next.view.dialog.BaseDialogFragment;
import me.ykrank.s1next.view.dialog.BlackListRemarkDialogFragment;
import me.ykrank.s1next.view.dialog.LogoutDialogFragment;
import me.ykrank.s1next.view.fragment.BaseFragment;
import me.ykrank.s1next.view.fragment.FavouriteListFragment;
import me.ykrank.s1next.view.fragment.PmFragment;
import me.ykrank.s1next.view.fragment.PmGroupsFragment;
import me.ykrank.s1next.view.fragment.WebLoginFragment;
import me.ykrank.s1next.view.fragment.setting.BackupPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.NetworkPreferenceFragment;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.hostcheck.BaseHostUrl;
import me.ykrank.s1next.widget.hostcheck.HttpDns;
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import okhttp3.OkHttpClient;

/**
 * Indicates the class where this module is going to inject dependencies
 * or the dependencies we want to get.
 */
@Singleton
@Component(modules = {AppModule.class, PrefModule.class, DbModule.class})
public interface AppComponent {

    Context getContext();

    SharedPreferences getSharedPreferences();

    NetworkPreferencesManager getNetworkPreferencesManager();

    BaseHostUrl getBaseHostUrl();

    HttpDns getHttpDns();

    OkHttpClient getOkHttpClient();

    S1Service getS1Service();

    EventBus getEventBus();

    User getUser();

    UserValidator getUserValidator();

    UserViewModel getUserViewModel();

    Wifi getWifi();

    ObjectMapper getJsonMapper();

    DataTrackAgent getDataTrackAgent();

    AppDaoSessionManager getAppDaoSessionManager();

    NoticeCheckTask getNoticeCheckTask();

    void inject(LogoutDialogFragment fragment);

    void inject(WebLoginFragment fragment);

    void inject(FavouriteListFragment favouriteListFragment);

    void inject(FavouriteAdapterDelegate favouriteAdapterDelegate);

    void inject(PmGroupsAdapterDelegate pmGroupsAdapterDelegate);

    void inject(PmFragment pmFragment);

    void inject(PmLeftAdapterDelegate pmLeftAdapterDelegate);

    void inject(PmRightAdapterDelegate pmRightAdapterDelegate);

    void inject(BlackListRemarkDialogFragment blackListRemarkDialogFragment);

    void inject(BaseDialogFragment baseDialogFragment);

    void inject(BackupPreferenceFragment backupPreferenceFragment);

    void inject(NoticeCheckTask noticeCheckTask);

    void inject(PmGroupsFragment pmGroupsFragment);

    void inject(NetworkPreferenceFragment networkPreferenceFragment);

    void inject(BaseFragment baseFragment);
}
