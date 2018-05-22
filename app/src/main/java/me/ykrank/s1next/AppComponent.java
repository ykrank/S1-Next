package me.ykrank.s1next;

import com.github.ykrank.androidtools.widget.EditorDiskCache;
import com.github.ykrank.androidtools.widget.hostcheck.HttpDns;
import com.github.ykrank.androidtools.widget.net.WifiBroadcastReceiver;

import org.jetbrains.annotations.NotNull;

import dagger.Component;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.ApiCacheProvider;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.api.app.AppService;
import me.ykrank.s1next.data.db.AppDaoSessionManager;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.data.db.DbModule;
import me.ykrank.s1next.data.db.HistoryDbWrapper;
import me.ykrank.s1next.data.db.ReadProgressDbWrapper;
import me.ykrank.s1next.data.db.ThreadDbWrapper;
import me.ykrank.s1next.task.AutoSignTask;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.view.activity.GalleryActivity;
import me.ykrank.s1next.view.activity.PostListGatewayActivity;
import me.ykrank.s1next.view.activity.SearchActivity;
import me.ykrank.s1next.view.activity.ThreadListActivity;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.view.adapter.SubForumArrayAdapter;
import me.ykrank.s1next.view.adapter.delegate.AppPostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.FavouriteAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ForumAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmGroupsAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmLeftAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmRightAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import me.ykrank.s1next.view.dialog.AppLoginDialogFragment;
import me.ykrank.s1next.view.dialog.BaseDialogFragment;
import me.ykrank.s1next.view.dialog.BlackListRemarkDialogFragment;
import me.ykrank.s1next.view.dialog.DiscardEditPromptDialogFragment;
import me.ykrank.s1next.view.dialog.LoginDialogFragment;
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment;
import me.ykrank.s1next.view.dialog.LogoutDialogFragment;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.view.dialog.VoteDialogFragment;
import me.ykrank.s1next.view.fragment.AppPostListPagerFragment;
import me.ykrank.s1next.view.fragment.BaseFragment;
import me.ykrank.s1next.view.fragment.BasePostFragment;
import me.ykrank.s1next.view.fragment.BaseViewPagerFragment;
import me.ykrank.s1next.view.fragment.EditPostFragment;
import me.ykrank.s1next.view.fragment.FavouriteListFragment;
import me.ykrank.s1next.view.fragment.ForumFragment;
import me.ykrank.s1next.view.fragment.GalleryFragment;
import me.ykrank.s1next.view.fragment.HistoryListFragment;
import me.ykrank.s1next.view.fragment.NewRateFragment;
import me.ykrank.s1next.view.fragment.NewThreadFragment;
import me.ykrank.s1next.view.fragment.NoteFragment;
import me.ykrank.s1next.view.fragment.PmFragment;
import me.ykrank.s1next.view.fragment.PmGroupsFragment;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.view.fragment.PostListPagerFragment;
import me.ykrank.s1next.view.fragment.ReplyFragment;
import me.ykrank.s1next.view.fragment.WebLoginFragment;
import me.ykrank.s1next.view.fragment.WebViewFragment;
import me.ykrank.s1next.view.fragment.setting.BackupPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.DownloadPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.GeneralPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.NetworkPreferenceFragment;
import me.ykrank.s1next.view.fragment.setting.ReadProgressPreferenceFragment;
import me.ykrank.s1next.view.internal.DrawerLayoutDelegateConcrete;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.AppActivityLifecycleCallbacks;
import me.ykrank.s1next.widget.glide.AppHttpStreamFetcher;
import me.ykrank.s1next.widget.glide.AvatarStreamFetcher;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;
import me.ykrank.s1next.widget.hostcheck.AppHostUrl;
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask;
import me.ykrank.s1next.widget.net.Image;
import okhttp3.OkHttpClient;

/**
 * Indicates the class where this module is going to inject dependencies
 * or the dependencies we want to get.
 */
@AppLife
@Component(dependencies = {PreAppComponent.class},
        modules = {DbModule.class, AppModule.class})
public interface AppComponent {

    PreAppComponent getPreAppComponent();

    AppHostUrl getBaseHostUrl();

    HttpDns getHttpDns();

    @Image
    OkHttpClient getImageOkHttpClient();

    S1Service getS1Service();

    AppService getAppService();

    ApiCacheProvider getApiCacheProvider();

    User getUser();

    UserValidator getUserValidator();

    UserViewModel getUserViewModel();

    NoticeCheckTask getNoticeCheckTask();

    EditorDiskCache getEditorDiskCache();

    AvatarUrlsCache getAvatarUrlsCache();

    AutoSignTask getAutoSignTask();

    //region DataBase
    AppDaoSessionManager getAppDaoSessionManager();

    BlackListDbWrapper getBlackListDbWrapper();

    ReadProgressDbWrapper getReadProgressDbWrapper();

    ThreadDbWrapper getThreadDbWrapper();

    HistoryDbWrapper getHistoryDbWrapper();
    //endregion

    void inject(BaseFragment baseFragment);

    void inject(BaseDialogFragment baseDialogFragment);

    void inject(LoginDialogFragment fragment);

    void inject(LogoutDialogFragment fragment);

    void inject(WebLoginFragment fragment);

    void inject(FavouriteListFragment favouriteListFragment);

    void inject(FavouriteAdapterDelegate favouriteAdapterDelegate);

    void inject(PmGroupsAdapterDelegate pmGroupsAdapterDelegate);

    void inject(PmFragment pmFragment);

    void inject(PmLeftAdapterDelegate pmLeftAdapterDelegate);

    void inject(PmRightAdapterDelegate pmRightAdapterDelegate);

    void inject(BlackListRemarkDialogFragment blackListRemarkDialogFragment);

    void inject(BackupPreferenceFragment backupPreferenceFragment);

    void inject(NoticeCheckTask noticeCheckTask);

    void inject(PmGroupsFragment pmGroupsFragment);

    void inject(NetworkPreferenceFragment networkPreferenceFragment);

    void inject(HistoryListFragment historyListFragment);

    void inject(PostListFragment postListFragment);

    void inject(BasePostFragment fragment);

    void inject(BaseActivity activity);

    void inject(ThreadListActivity activity);

    void inject(ForumActivity activity);

    void inject(PostAdapterDelegate delegate);

    void inject(DownloadPreferenceFragment fragment);

    void inject(WifiBroadcastReceiver receiver);

    void inject(PostListGatewayActivity activity);

    void inject(ThreadAdapterDelegate delegate);

    void inject(ThemeChangeDialogFragment fragment);

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

    void inject(GalleryActivity activity);

    void inject(GalleryFragment fragment);

    void inject(EditPostFragment fragment);

    void inject(SubForumArrayAdapter subForumArrayAdapter);

    void inject(ForumAdapterDelegate forumAdapterDelegate);

    void inject(ReadProgressPreferenceFragment readProgressPreferenceFragment);

    void inject(DiscardEditPromptDialogFragment discardEditPromptDialogFragment);

    void inject(WebViewFragment fragment);

    void inject(NoteFragment fragment);

    void inject(AppLoginDialogFragment fragment);

    void inject(LoginPromptDialogFragment fragment);

    void inject(AppPostAdapterDelegate appPostAdapterDelegate);

    void inject(AppPostListPagerFragment fragment);

    void inject(VoteDialogFragment fragment);

    void inject(ForumFragment fragment);

    void inject(@NotNull BaseViewPagerFragment baseViewPagerFragment);

    void inject(@NotNull AppActivityLifecycleCallbacks appActivityLifecycleCallbacks);
}
