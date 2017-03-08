package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.google.common.base.Optional;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.ThreadLink;
import me.ykrank.s1next.data.db.ReadProgressDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.ReadProgress;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.OnceClickUtil;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.widget.WifiBroadcastReceiver;

/**
 * An Activity which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of post lists.
 */
public final class PostListActivity extends BaseActivity
        implements WifiBroadcastReceiver.NeedMonitorWifi {
    public static final int RESULT_BLACKLIST = 11;

    private static final String ARG_THREAD = "thread";
    private static final String ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page";

    private static final String ARG_THREAD_LINK = "thread_link";
    private static final String ARG_COME_FROM_OTHER_APP = "come_from_other_app";

    private static final String ARG_READ_PROGRESS = "read_progress";

    @Inject
    ReadProgressPreferencesManager mReadProgressPrefManager;

    public static void startPostListActivity(Context context, Thread thread, boolean shouldGoToLastPage) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(ARG_THREAD, thread);
        intent.putExtra(ARG_SHOULD_GO_TO_LAST_PAGE, shouldGoToLastPage);

        if (context instanceof Activity)
            ((Activity) context).startActivityForResult(intent, RESULT_BLACKLIST);
        else context.startActivity(intent);
    }

    public static void startPostListActivity(Activity activity, ThreadLink threadLink) {
        // see android.text.style.URLSpan#onClick(View)
        String appId = activity.getIntent().getStringExtra(Browser.EXTRA_APPLICATION_ID);

        startPostListActivity(activity, threadLink, appId != null && !activity.getPackageName().equals(appId));
    }

    public static void startPostListActivity(Context context, ThreadLink threadLink, boolean comeFromOtherApp) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(ARG_THREAD_LINK, threadLink);
        intent.putExtra(ARG_COME_FROM_OTHER_APP, comeFromOtherApp);

        context.startActivity(intent);
    }

    public static void startPostListActivity(Context context, ReadProgress readProgress) {
        Intent intent = new Intent(context, PostListActivity.class);
        Thread thread = new Thread();
        thread.setId(String.valueOf(readProgress.getThreadId()));
        intent.putExtra(ARG_THREAD, thread);
        intent.putExtra(ARG_READ_PROGRESS, readProgress);

        context.startActivity(intent);
    }

    /**
     * 点击打开有读取进度的帖子
     *
     * @param view   点击焦点
     * @param thread 帖子信息
     * @return
     */
    public static Disposable clickStartPostListActivity(@NonNull View view, @NonNull Thread thread) {
        ReadProgressPreferencesManager preferencesManager = App.getPrefComponent().getReadProgressPreferencesManager();
        if (preferencesManager.isLoadAuto()) {
            return OnceClickUtil.onceClickObservable(view, 1000)
                    .observeOn(Schedulers.io())
                    .map(vo -> Optional.fromNullable(ReadProgressDbWrapper.getInstance().getWithThreadId(Integer.valueOf(thread.getId()))))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(progress -> {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, PostListActivity.class);
                        intent.putExtra(ARG_THREAD, thread);
                        intent.putExtra(ARG_READ_PROGRESS, progress.orNull());
                        if (context instanceof Activity) {
                            ((Activity) context).startActivityForResult(intent, RESULT_BLACKLIST);
                        } else {
                            context.startActivity(intent);
                        }
                    }, L::e);
        } else {
            return OnceClickUtil.setOnceClickLister(view, v -> {
                PostListActivity.startPostListActivity(v.getContext(), thread, false);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        disableDrawerIndicator();

        if (savedInstanceState == null) {
            Fragment fragment;
            Intent intent = getIntent();
            Thread thread = intent.getParcelableExtra(ARG_THREAD);
            ReadProgress progress = intent.getParcelableExtra(ARG_READ_PROGRESS);
            if (thread == null) {//通过链接打开
                fragment = PostListFragment.newInstance(intent.getParcelableExtra(ARG_THREAD_LINK));
            } else if (progress != null) {//有进度信息
                fragment = PostListFragment.newInstance(thread, progress);
            } else {//没有进度信息
                fragment = PostListFragment.newInstance(thread, intent.getBooleanExtra(
                        ARG_SHOULD_GO_TO_LAST_PAGE, false));
            }
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    PostListFragment.TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().getBooleanExtra(ARG_COME_FROM_OTHER_APP, false)) {
                    ForumActivity.start(this);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (hasFocus) {
//                getWindow().addFlags(FLAG_TRANSLUCENT_NAVIGATION);
//                getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            }
//        }
    }

    @Override
    public boolean isTranslucent() {
        return true;
    }
}
