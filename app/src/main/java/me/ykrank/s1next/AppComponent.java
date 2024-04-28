package me.ykrank.s1next;

import com.github.ykrank.androidtools.widget.EditorDiskCache;
import com.github.ykrank.androidtools.widget.net.WifiBroadcastReceiver;

import dagger.Component;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.ApiCacheProvider;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.api.app.AppService;
import me.ykrank.s1next.data.db.AppDatabaseManager;
import me.ykrank.s1next.data.db.biz.BlackListBiz;
import me.ykrank.s1next.data.db.biz.BlackWordBiz;
import me.ykrank.s1next.data.db.DbModule;
import me.ykrank.s1next.data.db.biz.HistoryBiz;
import me.ykrank.s1next.data.db.biz.ReadProgressBiz;
import me.ykrank.s1next.data.db.biz.ThreadBiz;
import me.ykrank.s1next.task.AutoSignTask;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.activity.ForumActivity;
import me.ykrank.s1next.view.activity.GalleryActivity;
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
import me.ykrank.s1next.view.page.login.BaseLoginDialogFragment;
import me.ykrank.s1next.view.page.post.adapter.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import me.ykrank.s1next.view.page.login.AppLoginDialogFragment;
import me.ykrank.s1next.view.dialog.BlackListRemarkDialogFragment;
import me.ykrank.s1next.view.dialog.DiscardEditPromptDialogFragment;
import me.ykrank.s1next.view.dialog.LoadBlackListFromWebDialogFragment;
import me.ykrank.s1next.view.page.login.LoginDialogFragment;
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment;
import me.ykrank.s1next.view.dialog.LogoutDialogFragment;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.view.dialog.VoteDialogFragment;
import me.ykrank.s1next.view.fragment.BaseFragment;
import me.ykrank.s1next.view.page.post.postedit.BasePostEditFragment;
import me.ykrank.s1next.view.fragment.BaseViewPagerFragment;
import me.ykrank.s1next.view.fragment.DarkRoomFragment;
import me.ykrank.s1next.view.fragment.EditPostFragment;
import me.ykrank.s1next.view.fragment.FavouriteListFragment;
import me.ykrank.s1next.view.fragment.ForumFragment;
import me.ykrank.s1next.view.fragment.GalleryFragment;
import me.ykrank.s1next.view.fragment.HistoryListFragment;
import me.ykrank.s1next.view.page.post.postedit.toolstab.ImageUploadFragment;
import me.ykrank.s1next.view.fragment.NewRateFragment;
import me.ykrank.s1next.view.fragment.NewReportFragment;
import me.ykrank.s1next.view.page.post.postedit.NewThreadFragment;
import me.ykrank.s1next.view.fragment.NoteFragment;
import me.ykrank.s1next.view.fragment.PmFragment;
import me.ykrank.s1next.view.fragment.PmGroupsFragment;
import me.ykrank.s1next.view.page.post.postedit.ReplyFragment;
import me.ykrank.s1next.view.fragment.ThreadListFragment;
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment;
import me.ykrank.s1next.view.fragment.WebLoginFragment;
import me.ykrank.s1next.view.fragment.WebViewFragment;
import me.ykrank.s1next.view.page.post.adapter.PostBlackAdapterDelegate;
import me.ykrank.s1next.view.page.setting.fragment.BackupPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.DownloadPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.GeneralPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.NetworkPreferenceFragment;
import me.ykrank.s1next.view.page.setting.fragment.ReadPreferenceFragment;
import me.ykrank.s1next.view.internal.DrawerLayoutDelegateConcrete;
import me.ykrank.s1next.view.page.app.AppPostListFragment;
import me.ykrank.s1next.view.page.app.AppPostListPagerFragment;
import me.ykrank.s1next.view.page.post.postlist.PostListActivity;
import me.ykrank.s1next.view.page.post.postlist.PostListFragment;
import me.ykrank.s1next.view.page.post.postlist.PostListGatewayActivity;
import me.ykrank.s1next.view.page.post.postlist.PostListPagerFragment;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.AppActivityLifecycleCallbacks;
import me.ykrank.s1next.widget.download.ImageDownloadManager;
import me.ykrank.s1next.widget.glide.AvatarStreamFetcher;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;
import me.ykrank.s1next.widget.glide.MultiThreadHttpStreamFetcher;
import me.ykrank.s1next.widget.hostcheck.AppHostUrl;
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask;
import me.ykrank.s1next.widget.net.Image;
import okhttp3.Dns;
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

    Dns getHttpDns();

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

    ImageDownloadManager getImageDownloadManager();

    //region DataBase
    AppDatabaseManager getAppDatabaseManager();

    BlackListBiz getBlackListBiz();

    BlackWordBiz getBlackWordBiz();

    ReadProgressBiz getReadProgressBiz();

    ThreadBiz getThreadBiz();

    HistoryBiz getHistoryBiz();
    //endregion

    void inject(BaseFragment baseFragment);

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


    void inject(BasePostEditFragment fragment);

    void inject(BaseActivity activity);

    void inject(PostListActivity activity);

    void inject(ThreadListActivity activity);

    void inject(ForumActivity activity);

    void inject(PostAdapterDelegate delegate);

    void inject(PostBlackAdapterDelegate delegate);

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

    void inject(AvatarStreamFetcher avatarStreamFetcher);

    void inject(DrawerLayoutDelegateConcrete drawerLayoutDelegateConcrete);

    void inject(GalleryActivity activity);

    void inject(GalleryFragment fragment);

    void inject(EditPostFragment fragment);

    void inject(SubForumArrayAdapter subForumArrayAdapter);

    void inject(ForumAdapterDelegate forumAdapterDelegate);

    void inject(ReadPreferenceFragment readProgressPreferenceFragment);

    void inject(DiscardEditPromptDialogFragment discardEditPromptDialogFragment);

    void inject(WebViewFragment fragment);

    void inject(NoteFragment fragment);

    void inject(AppLoginDialogFragment fragment);
    void inject(LoginPromptDialogFragment fragment);

    void inject(AppPostAdapterDelegate appPostAdapterDelegate);

    void inject(AppPostListPagerFragment fragment);

    void inject(VoteDialogFragment fragment);

    void inject(ForumFragment fragment);

    void inject(BaseViewPagerFragment fragment);

    void inject(AppActivityLifecycleCallbacks appActivityLifecycleCallbacks);

    void inject(ThreadListPagerFragment fragment);

    void inject(ImageUploadFragment fragment);

    void inject(NewReportFragment fragment);

    void inject(DarkRoomFragment fragment);

    void inject(LoadBlackListFromWebDialogFragment fragment);

    void inject(ThreadListFragment fragment);

    void inject(MultiThreadHttpStreamFetcher multiThreadHttpStreamFetcher);

    void inject(AppPostListFragment fragment);
}
