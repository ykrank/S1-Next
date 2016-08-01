package me.ykrank.s1next;

import javax.inject.Singleton;

import dagger.Component;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import me.ykrank.s1next.view.dialog.LogoutDialogFragment;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.view.fragment.BasePostFragment;
import me.ykrank.s1next.view.fragment.NewThreadFragment;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.view.fragment.PostListPagerFragment;
import me.ykrank.s1next.view.fragment.ReplyFragment;
import me.ykrank.s1next.view.fragment.setting.GeneralPreferenceFragment;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.WifiBroadcastReceiver;
import okhttp3.OkHttpClient;

/**
 * Indicates the class where this module is going to inject dependencies
 * or the dependencies we want to get.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    OkHttpClient getOkHttpClient();

    S1Service getS1Service();

    EventBus getEventBus();

    User getUser();

    UserValidator getUserValidator();

    UserViewModel getUserViewModel();

    GeneralPreferencesManager getGeneralPreferencesManager();

    DownloadPreferencesManager getDownloadPreferencesManager();

    ThemeManager getThemeManager();

    ReadProgressPreferencesManager getReadProgressPreferencesManager();

    void inject(BaseActivity activity);

    void inject(PostListFragment fragment);

    void inject(PostListPagerFragment fragment);

    void inject(ReplyFragment fragment);

    void inject(BasePostFragment fragment);

    void inject(NewThreadFragment fragment);

    void inject(GeneralPreferenceFragment fragment);

    void inject(LogoutDialogFragment fragment);

    void inject(ThemeChangeDialogFragment fragment);

    void inject(ThreadAdapterDelegate delegate);

    void inject(PostAdapterDelegate delegate);

    void inject(WifiBroadcastReceiver wifiBroadcastReceiver);
}
