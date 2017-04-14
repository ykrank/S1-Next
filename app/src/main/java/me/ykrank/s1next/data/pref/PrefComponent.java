package me.ykrank.s1next.data.pref;

import dagger.Component;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.view.activity.GalleryActivity;
import me.ykrank.s1next.view.activity.PostListGatewayActivity;
import me.ykrank.s1next.view.activity.SearchActivity;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.view.fragment.BasePostFragment;
import me.ykrank.s1next.view.fragment.EditPostFragment;
import me.ykrank.s1next.view.fragment.NewRateFragment;
import me.ykrank.s1next.view.fragment.NewThreadFragment;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.view.fragment.PostListPagerFragment;
import me.ykrank.s1next.view.fragment.ReplyFragment;
import me.ykrank.s1next.view.fragment.setting.DownloadPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.GeneralPreferenceFragment;
import me.ykrank.s1next.view.internal.DrawerLayoutDelegateConcrete;
import me.ykrank.s1next.widget.WifiBroadcastReceiver;
import me.ykrank.s1next.widget.glide.AppHttpStreamFetcher;
import me.ykrank.s1next.widget.glide.AvatarStreamFetcher;

/**
 * Created by ykrank on 2016/11/4.
 */
@PrefScope
@Component(dependencies = AppComponent.class, modules = PrefModule.class)
public interface PrefComponent {
    ThemeManager getThemeManager();

    GeneralPreferencesManager getGeneralPreferencesManager();

    DownloadPreferencesManager getDownloadPreferencesManager();

    ReadProgressPreferencesManager getReadProgressPreferencesManager();

    DataPreferencesManager getDataPreferencesManager();

    void inject(BasePostFragment fragment);

    void inject(BaseActivity activity);

    void inject(ForumActivity activity);

    void inject(PostAdapterDelegate delegate);

    void inject(DownloadPreferenceFragment fragment);

    void inject(WifiBroadcastReceiver receiver);

    void inject(PostListGatewayActivity activity);

    void inject(ThreadAdapterDelegate delegate);

    void inject(ThemeChangeDialogFragment fragment);

    void inject(PostListFragment postListFragment);

    void inject(SearchActivity activity);

    void inject(GeneralPreferenceFragment fragment);

    void inject(ReplyFragment fragment);

    void inject(NewThreadFragment fragment);

    void inject(UserHomeActivity userHomeActivity);

    void inject(PostListPagerFragment postListPagerFragment);

    void inject(NewRateFragment newRateFragment);

    void inject(AppHttpStreamFetcher appStreamLoader);

    void inject(AvatarStreamFetcher avatarStreamFetcher);

    void inject(DrawerLayoutDelegateConcrete drawerLayoutDelegateConcrete);

    void inject(GalleryActivity galleryActivity);

    void inject(EditPostFragment fragment);
}
