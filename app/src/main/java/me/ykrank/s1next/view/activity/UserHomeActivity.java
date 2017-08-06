package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.ykrank.androidautodispose.AndroidRxDispose;
import com.github.ykrank.androidlifecycle.event.ActivityEvent;
import com.google.common.base.Optional;

import javax.inject.Inject;

import io.reactivex.Single;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.model.Profile;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.databinding.ActivityHomeBinding;
import me.ykrank.s1next.util.AnimUtils;
import me.ykrank.s1next.util.ContextUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.util.TransitionUtils;
import me.ykrank.s1next.view.event.BlackListChangeEvent;
import me.ykrank.s1next.view.internal.BlacklistMenuAction;
import me.ykrank.s1next.widget.AppBarOffsetChangedListener;
import me.ykrank.s1next.widget.glide.model.ImageInfo;
import me.ykrank.s1next.widget.track.event.ViewHomeTrackEvent;

/**
 * Created by ykrank on 2017/1/8.
 */

public class UserHomeActivity extends BaseActivity {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.71f;
    private static final int TITLE_ANIMATIONS_DURATION = 300;

    private static final String ARG_UID = "uid";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_IMAGE_INFO = "image_info";

    @Inject
    S1Service s1Service;

    private ActivityHomeBinding binding;
    private String uid, name;
    private boolean isInBlacklist;
    private MenuItem blacklistMenu;

    public static void start(Context context, String uid, String userName) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        intent.putExtra(ARG_UID, uid);
        intent.putExtra(ARG_USERNAME, userName);
        context.startActivity(intent);
    }

    public static void start(Context context, String uid, String userName, View avatarView) {
        //@see http://stackoverflow.com/questions/31381385/nullpointerexception-drawable-setbounds-probably-due-to-fragment-transitions#answer-31383033
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            start(context, uid, userName);
            return;
        }
        Context baseContext = ContextUtils.getBaseContext(context);
        if (!(baseContext instanceof Activity)) {
            L.leaveMsg("uid:" + uid);
            L.leaveMsg("userName:" + userName);
            L.report(new IllegalStateException("UserHomeActivity start error: context not instance of activity"));
            return;
        }
        ImageInfo imageInfo = (ImageInfo) avatarView.getTag(R.id.tag_drawable_info);
        Activity activity = (Activity) baseContext;
        Intent intent = new Intent(activity, UserHomeActivity.class);
        intent.putExtra(ARG_UID, uid);
        intent.putExtra(ARG_USERNAME, userName);
        if (imageInfo != null) {
            intent.putExtra(ARG_IMAGE_INFO, imageInfo);
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, avatarView, activity.getString(R.string.transition_avatar));
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        uid = getIntent().getStringExtra(ARG_UID);
        name = getIntent().getStringExtra(ARG_USERNAME);
        ImageInfo thumbImageInfo = getIntent().getParcelableExtra(ARG_IMAGE_INFO);
        trackAgent.post(new ViewHomeTrackEvent(uid, name));
        L.leaveMsg("UserHomeActivity##uid:" + uid + ",name:" + name);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setDownloadPreferencesManager(mDownloadPreferencesManager);
        binding.setBig(true);
        binding.setPreLoad(true);
        binding.setThumb(thumbImageInfo == null ? null : thumbImageInfo.getUrl());
        Profile profile = new Profile();
        profile.setHomeUid(uid);
        profile.setHomeUsername(name);
        binding.setData(profile);

        binding.appBar.addOnOffsetChangedListener(new AppBarOffsetChangedListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int oldVerticalOffset, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float oldPercentage = (float) Math.abs(oldVerticalOffset) / (float) maxScroll;
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                if (oldPercentage < PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR && percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                    //Move up
                    AnimUtils.startAlphaAnimation(binding.toolbarTitle, TITLE_ANIMATIONS_DURATION, View.VISIBLE);
                } else if (oldPercentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR && percentage < PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                    //Move down
                    AnimUtils.startAlphaAnimation(binding.toolbarTitle, TITLE_ANIMATIONS_DURATION, View.INVISIBLE);
                }
            }
        });

        binding.avatar.setOnClickListener(v -> {
            String bigAvatarUrl = Api.getAvatarBigUrl(uid);
            GalleryActivity.Companion.start(v.getContext(), bigAvatarUrl);
        });

        binding.ivNewPm.setOnClickListener(v -> NewPmActivity.Companion.startNewPmActivityForResultMessage(this,
                binding.getData().getHomeUid(), binding.getData().getHomeUsername()));

        binding.tvFriends.setOnClickListener(v -> FriendListActivity.start(this, uid, name));

        binding.tvThreads.setOnClickListener(v -> UserThreadActivity.start(this, uid, name));

        binding.tvReplies.setOnClickListener(v -> UserReplyActivity.start(this, uid, name));

        setupImage();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        blacklistMenu = menu.findItem(R.id.menu_blacklist);
        refreshBlacklistMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_blacklist:
                if (isInBlacklist) {
                    BlacklistMenuAction.removeBlacklist(mRxBus, Integer.valueOf(uid), name);
                } else {
                    BlacklistMenuAction.addBlacklist(this, Integer.valueOf(uid), name);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRxBus.get()
                .ofType(BlackListChangeEvent.class)
                .to(AndroidRxDispose.withObservable(this, ActivityEvent.PAUSE))
                .subscribe(blackListEvent -> {
                    BlackListDbWrapper dbWrapper = BlackListDbWrapper.getInstance();
                    if (blackListEvent.isAdd()) {
                        Single.just(true)
                                .doOnSuccess(b -> dbWrapper.saveDefaultBlackList(
                                        blackListEvent.getAuthorPostId(), blackListEvent.getAuthorPostName(),
                                        blackListEvent.getRemark()))
                                .compose(RxJavaUtil.iOSingleTransformer())
                                .subscribe(this::afterBlackListChange, L::report);
                    } else {
                        Single.just(false)
                                .doOnSuccess(b -> dbWrapper.delDefaultBlackList(blackListEvent.getAuthorPostId(),
                                        blackListEvent.getAuthorPostName()))
                                .compose(RxJavaUtil.iOSingleTransformer())
                                .subscribe(this::afterBlackListChange, L::report);
                    }
                });
    }

    private void afterBlackListChange(boolean isAdd) {
        showShortToast(isAdd ? R.string.blacklist_add_success : R.string.blacklist_remove_success);
        refreshBlacklistMenu();
    }

    @Override
    public boolean isTranslucent() {
        return true;
    }

    private void setupImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(new TransitionUtils.TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    binding.setBig(true);
                    binding.setPreLoad(false);
                }
            });
        } else {
            binding.setBig(true);
            binding.setPreLoad(false);
        }
    }

    private void loadData() {
        s1Service.getProfile(binding.getData().getHomeUid())
                .compose(RxJavaUtil.iOTransformer())
                .to(AndroidRxDispose.withObservable(this, ActivityEvent.DESTROY))
                .subscribe(wrapper -> {
                    binding.setData(wrapper.getData());
                }, L::e);
    }

    @MainThread
    private void refreshBlacklistMenu() {
        if (blacklistMenu == null) {
            return;
        }
        BlackListDbWrapper wrapper = BlackListDbWrapper.getInstance();
        Single.just(Optional.fromNullable(wrapper.getBlackListDefault(Integer.valueOf(uid), name)))
                .compose(RxJavaUtil.iOSingleTransformer())
                .subscribe(blackListOptional -> {
                    if (blackListOptional.isPresent()) {
                        isInBlacklist = true;
                        blacklistMenu.setTitle(R.string.menu_blacklist_remove);
                    } else {
                        isInBlacklist = false;
                        blacklistMenu.setTitle(R.string.menu_blacklist_add);
                    }
                }, L::report);
    }
}
