package me.ykrank.s1next

import com.github.ykrank.androidtools.widget.EditorDiskCache
import com.github.ykrank.androidtools.widget.net.WifiBroadcastReceiver
import dagger.Component
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.cache.biz.CacheBiz
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.DbModule
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.biz.BlackWordBiz
import me.ykrank.s1next.data.db.biz.HistoryBiz
import me.ykrank.s1next.data.db.biz.LoginUserBiz
import me.ykrank.s1next.data.db.biz.ReadProgressBiz
import me.ykrank.s1next.data.db.biz.ThreadBiz
import me.ykrank.s1next.task.AutoSignTask
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.activity.ForumActivity
import me.ykrank.s1next.view.activity.GalleryActivity
import me.ykrank.s1next.view.activity.SearchActivity
import me.ykrank.s1next.view.activity.ThreadListActivity
import me.ykrank.s1next.view.activity.UserHomeActivity
import me.ykrank.s1next.view.adapter.SubForumArrayAdapter
import me.ykrank.s1next.view.adapter.delegate.AppPostAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.FavouriteAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.ForumAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.PmGroupsAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.PmLeftAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.PmRightAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate
import me.ykrank.s1next.view.dialog.DiscardEditPromptDialogFragment
import me.ykrank.s1next.view.dialog.LoadBlackListFromWebDialogFragment
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.dialog.LogoutDialogFragment
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment
import me.ykrank.s1next.view.dialog.VoteDialogFragment
import me.ykrank.s1next.view.fragment.BaseFragment
import me.ykrank.s1next.view.fragment.BaseViewPagerFragment
import me.ykrank.s1next.view.fragment.DarkRoomFragment
import me.ykrank.s1next.view.fragment.EditPostFragment
import me.ykrank.s1next.view.fragment.FavouriteListFragment
import me.ykrank.s1next.view.fragment.ForumFragment
import me.ykrank.s1next.view.fragment.GalleryFragment
import me.ykrank.s1next.view.fragment.HistoryListFragment
import me.ykrank.s1next.view.fragment.NewRateFragment
import me.ykrank.s1next.view.fragment.NewReportFragment
import me.ykrank.s1next.view.fragment.NoteFragment
import me.ykrank.s1next.view.fragment.PmFragment
import me.ykrank.s1next.view.fragment.PmGroupsFragment
import me.ykrank.s1next.view.fragment.ThreadListFragment
import me.ykrank.s1next.view.fragment.ThreadListPagerFragment
import me.ykrank.s1next.view.fragment.WebLoginFragment
import me.ykrank.s1next.view.fragment.WebViewFragment
import me.ykrank.s1next.view.internal.DrawerLayoutDelegateConcrete
import me.ykrank.s1next.view.page.app.AppPostListFragment
import me.ykrank.s1next.view.page.app.AppPostListPagerFragment
import me.ykrank.s1next.view.page.login.AppLoginDialogFragment
import me.ykrank.s1next.view.page.login.BaseLoginFragment
import me.ykrank.s1next.view.page.post.adapter.PostAdapterDelegate
import me.ykrank.s1next.view.page.post.adapter.PostBlackAdapterDelegate
import me.ykrank.s1next.view.page.post.postedit.BasePostEditFragment
import me.ykrank.s1next.view.page.post.postedit.NewThreadFragment
import me.ykrank.s1next.view.page.post.postedit.ReplyFragment
import me.ykrank.s1next.view.page.post.postedit.toolstab.ImageUploadFragment
import me.ykrank.s1next.view.page.post.postlist.PostListActivity
import me.ykrank.s1next.view.page.post.postlist.PostListFragment
import me.ykrank.s1next.view.page.post.postlist.PostListGatewayActivity
import me.ykrank.s1next.view.page.post.postlist.PostListPagerFragment
import me.ykrank.s1next.view.page.post.prefetch.ThreadPrefetchDialogFragment
import me.ykrank.s1next.view.page.setting.blacklist.BlackListRemarkDialogFragment
import me.ykrank.s1next.view.page.setting.fragment.BackupPreferenceFragment
import me.ykrank.s1next.view.page.setting.fragment.DownloadPreferenceFragment
import me.ykrank.s1next.view.page.setting.fragment.GeneralPreferenceFragment
import me.ykrank.s1next.view.page.setting.fragment.NetworkPreferenceFragment
import me.ykrank.s1next.view.page.setting.fragment.ReadPreferenceFragment
import me.ykrank.s1next.viewmodel.UserViewModel
import me.ykrank.s1next.widget.AppActivityLifecycleCallbacks
import me.ykrank.s1next.widget.glide.AppHttpStreamFetcher
import me.ykrank.s1next.widget.glide.AvatarFailUrlsCache
import me.ykrank.s1next.widget.glide.AvatarStreamFetcher
import me.ykrank.s1next.widget.hostcheck.AppHostUrl
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask
import me.ykrank.s1next.widget.net.Image
import me.ykrank.s1next.widget.saf.SAFFragment
import okhttp3.Dns
import okhttp3.OkHttpClient

/**
 * Indicates the class where this module is going to inject dependencies
 * or the dependencies we want to get.
 */
@AppLife
@Component(
    dependencies = [PreAppComponent::class],
    modules = [DbModule::class, AppModule::class, BuildTypeModule::class]
)
interface AppComponent {
    val preAppComponent: PreAppComponent
    val baseHostUrl: AppHostUrl
    val httpDns: Dns

    @get:Image
    val imageOkHttpClient: OkHttpClient
    val s1Service: S1Service
    val appService: AppService
    val apiCacheProvider: ApiCacheProvider
    val user: User
    val userValidator: UserValidator
    val userViewModel: UserViewModel
    val noticeCheckTask: NoticeCheckTask
    val editorDiskCache: EditorDiskCache

    val avatarFailUrlsCache: AvatarFailUrlsCache
    val autoSignTask: AutoSignTask

    //region DataBase
    val appDatabaseManager: AppDatabaseManager

    val blackListBiz: BlackListBiz
    val blackWordBiz: BlackWordBiz
    val readProgressBiz: ReadProgressBiz
    val threadBiz: ThreadBiz
    val historyBiz: HistoryBiz
    val loginUserBiz: LoginUserBiz
    val cacheBiz: CacheBiz

    //endregion
    fun inject(baseFragment: BaseFragment)
    fun inject(fragment: LogoutDialogFragment)
    fun inject(fragment: WebLoginFragment)
    fun inject(favouriteListFragment: FavouriteListFragment)
    fun inject(favouriteAdapterDelegate: FavouriteAdapterDelegate)
    fun inject(pmGroupsAdapterDelegate: PmGroupsAdapterDelegate)
    fun inject(pmFragment: PmFragment)
    fun inject(pmLeftAdapterDelegate: PmLeftAdapterDelegate)
    fun inject(pmRightAdapterDelegate: PmRightAdapterDelegate)
    fun inject(blackListRemarkDialogFragment: BlackListRemarkDialogFragment)
    fun inject(backupPreferenceFragment: BackupPreferenceFragment)
    fun inject(noticeCheckTask: NoticeCheckTask)
    fun inject(pmGroupsFragment: PmGroupsFragment)
    fun inject(networkPreferenceFragment: NetworkPreferenceFragment)
    fun inject(historyListFragment: HistoryListFragment)
    fun inject(postListFragment: PostListFragment)
    fun inject(fragment: BasePostEditFragment)
    fun inject(activity: BaseActivity)
    fun inject(activity: PostListActivity)
    fun inject(activity: ThreadListActivity)
    fun inject(activity: ForumActivity)
    fun inject(delegate: PostAdapterDelegate)
    fun inject(delegate: PostBlackAdapterDelegate)
    fun inject(fragment: DownloadPreferenceFragment)
    fun inject(receiver: WifiBroadcastReceiver)
    fun inject(activity: PostListGatewayActivity)
    fun inject(delegate: ThreadAdapterDelegate)
    fun inject(fragment: ThemeChangeDialogFragment)
    fun inject(activity: SearchActivity)
    fun inject(fragment: GeneralPreferenceFragment)
    fun inject(fragment: ReplyFragment)
    fun inject(fragment: NewThreadFragment)
    fun inject(userHomeActivity: UserHomeActivity)
    fun inject(postListPagerFragment: PostListPagerFragment)
    fun inject(newRateFragment: NewRateFragment)
    fun inject(avatarStreamFetcher: AvatarStreamFetcher)
    fun inject(drawerLayoutDelegateConcrete: DrawerLayoutDelegateConcrete)
    fun inject(activity: GalleryActivity)
    fun inject(fragment: GalleryFragment)
    fun inject(fragment: EditPostFragment)
    fun inject(subForumArrayAdapter: SubForumArrayAdapter)
    fun inject(forumAdapterDelegate: ForumAdapterDelegate)
    fun inject(readProgressPreferenceFragment: ReadPreferenceFragment)
    fun inject(discardEditPromptDialogFragment: DiscardEditPromptDialogFragment)
    fun inject(fragment: WebViewFragment)
    fun inject(fragment: NoteFragment)
    fun inject(fragment: AppLoginDialogFragment)
    fun inject(fragment: LoginPromptDialogFragment)
    fun inject(appPostAdapterDelegate: AppPostAdapterDelegate)
    fun inject(fragment: AppPostListPagerFragment)
    fun inject(fragment: VoteDialogFragment)
    fun inject(fragment: ForumFragment)
    fun inject(fragment: BaseViewPagerFragment)
    fun inject(appActivityLifecycleCallbacks: AppActivityLifecycleCallbacks)
    fun inject(fragment: ThreadListPagerFragment)
    fun inject(fragment: ImageUploadFragment)
    fun inject(fragment: NewReportFragment)
    fun inject(fragment: DarkRoomFragment)
    fun inject(fragment: LoadBlackListFromWebDialogFragment)
    fun inject(fragment: ThreadListFragment)
    fun inject(appHttpStreamFetcher: AppHttpStreamFetcher)
    fun inject(fragment: AppPostListFragment)
    fun inject(fragment: BaseLoginFragment)
    fun inject(fragment: SAFFragment)
    fun inject(fragment: ThreadPrefetchDialogFragment)

}
