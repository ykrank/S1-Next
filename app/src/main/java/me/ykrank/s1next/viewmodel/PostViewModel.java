package me.ykrank.s1next.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.event.BlackListAddEvent;
import me.ykrank.s1next.util.ActivityUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.view.dialog.BlackListRemarkDialogFragment;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;
import me.ykrank.s1next.widget.track.event.BlackListTrackEvent;

public final class PostViewModel {

    public final ObservableField<Post> post = new ObservableField<>();

    public void onClick(View v) {
        //Clear avatar false cache
        AvatarUrlsCache.clearUserAvatarCache(post.get().getAuthorId());
        //个人主页
        UserHomeActivity.start(v.getContext(), post.get().getAuthorId(), post.get().getAuthorName(), v);
    }

    public boolean onLongClick(View v, EventBus eventBus) {
        //长按显示抹布菜单
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        Post postData = post.get();
        popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
            switch (menuitem.getItemId()) {
                case R.id.menu_popup_blacklist:
                    if (menuitem.getTitle().equals(v.getContext().getString(R.string.menu_blacklist_remove))) {
                        App.get().getTrackAgent().post(new BlackListTrackEvent(false, postData.getAuthorId(), postData.getAuthorName()));
                        eventBus.post(new BlackListAddEvent(Integer.valueOf(postData.getAuthorId()),
                                postData.getAuthorName(), null, false));
                    } else {
                        Context context = ActivityUtils.getBaseContext(v.getContext());
                        if (context instanceof FragmentActivity) {
                            BlackListRemarkDialogFragment.newInstance(Integer.valueOf(postData.getAuthorId()),
                                    postData.getAuthorName()).show(((FragmentActivity) context).getSupportFragmentManager(),
                                    BlackListRemarkDialogFragment.TAG);
                        } else {
                            L.report(new IllegalStateException("抹布时头像Context不为FragmentActivity" + context));
                        }
                    }
                    return true;
                default:
                    return false;
            }
        });
        popup.inflate(R.menu.popup_blacklist);
        if (postData.isHide()) {
            popup.getMenu().findItem(R.id.menu_popup_blacklist).setTitle(R.string.menu_blacklist_remove);
        }
        popup.show();
        return true;
    }
}
