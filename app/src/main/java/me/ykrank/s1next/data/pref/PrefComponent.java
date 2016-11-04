package me.ykrank.s1next.data.pref;

import dagger.Component;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.activity.PostListGatewayActivity;
import me.ykrank.s1next.view.activity.SearchActivity;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.view.fragment.BasePostFragment;
import me.ykrank.s1next.view.fragment.NewThreadFragment;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.view.fragment.ReplyFragment;
import me.ykrank.s1next.view.fragment.setting.DownloadPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.GeneralPreferenceFragment;
import me.ykrank.s1next.widget.OkHttpStreamFetcher;
import me.ykrank.s1next.widget.WifiBroadcastReceiver;

/**
 * Created by ykrank on 2016/11/4.
 */
@PrefScope
@Component(dependencies = AppComponent.class, modules = PrefModule.class)
public interface PrefComponent{
    ThemeManager getThemeManager();
    
    GeneralPreferencesManager getGeneralPreferencesManager();

    DownloadPreferencesManager getDownloadPreferencesManager();

    ReadProgressPreferencesManager getReadProgressPreferencesManager();

    void inject(BasePostFragment fragment);

    void inject(BaseActivity activity);

    void inject(PostAdapterDelegate delegate);
    
    void inject(DownloadPreferenceFragment fragment);
    
    void inject(OkHttpStreamFetcher fetcher);
    
    void inject(WifiBroadcastReceiver receiver);
    
    void inject(PostListGatewayActivity activity);

    void inject(ThreadAdapterDelegate delegate);

    void inject(ThemeChangeDialogFragment fragment);

    void inject(PostListFragment postListFragment);

    void inject(SearchActivity activity);

    void inject(GeneralPreferenceFragment fragment);

    void inject(ReplyFragment fragment);

    void inject(NewThreadFragment fragment);
}
