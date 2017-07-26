package me.ykrank.s1next.viewmodel;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.app.model.AppPost;
import me.ykrank.s1next.data.api.app.model.AppThread;
import me.ykrank.s1next.util.ContextUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.view.event.EditPostEvent;
import me.ykrank.s1next.view.event.QuoteEvent;
import me.ykrank.s1next.view.event.RateEvent;
import me.ykrank.s1next.view.internal.BlacklistMenuAction;
import me.ykrank.s1next.widget.RxBus;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;

public final class PostViewModel {

    public final ObservableField<AppPost> post = new ObservableField<>();
    public final ObservableField<AppThread> thread = new ObservableField<>();
    public final ObservableField<CharSequence> floor = new ObservableField<>();
    private final RxBus rxBus;
    private final User user;


    public PostViewModel(RxBus rxBus, User user) {
        this.rxBus = rxBus;
        this.user = user;
        post.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                floor.set(getPostFloor());
            }
        });
    }

    private CharSequence getPostFloor() {
        AppPost p = post.get();
        if (p == null) {
            return null;
        }
        String text = "#" + p.getPosition();
        Spannable spannable = new SpannableString(text);
        URLSpan urlSpan = new URLSpan(StringUtils.EMPTY) {
            @Override
            public void onClick(@NonNull View widget) {
                showFloorActionMenu(widget);
            }
        };
        spannable.setSpan(urlSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public void onAvatarClick(View v) {
        //Clear avatar false cache
        AvatarUrlsCache.clearUserAvatarCache("" + post.get().getAuthorId());
        //个人主页
        UserHomeActivity.start(v.getContext(), "" + post.get().getAuthorId(), post.get().getAuthor(), v);
    }

    public boolean onLongClick(View v) {
        //长按显示抹布菜单
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        AppPost postData = post.get();
        popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
            switch (menuitem.getItemId()) {
                case R.id.menu_popup_blacklist:
                    if (menuitem.getTitle().equals(v.getContext().getString(R.string.menu_blacklist_remove))) {
                        BlacklistMenuAction.removeBlacklist(rxBus, postData.getAuthorId(), postData.getAuthor());
                    } else {
                        Context context = ContextUtils.getBaseContext(v.getContext());
                        if (context instanceof FragmentActivity) {
                            BlacklistMenuAction.addBlacklist((FragmentActivity) context,
                                    postData.getAuthorId(), postData.getAuthor());
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
        if (postData.getHide()) {
            popup.getMenu().findItem(R.id.menu_popup_blacklist).setTitle(R.string.menu_blacklist_remove);
        }
        popup.show();
        return true;
    }

    //click floor textView, show popup menu
    private void showFloorActionMenu(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
            switch (menuitem.getItemId()) {
                case R.id.menu_popup_reply:
                    onReplyClick(v);
                    return true;
                case R.id.menu_popup_rate:
                    onRateClick(v);
                    return true;
                case R.id.menu_popup_edit:
                    onEditClick(v);
                    return true;
                default:
                    return false;
            }
        });
        popup.inflate(R.menu.popup_post_floor);

        MenuItem editPostMenuItem = popup.getMenu().findItem(R.id.menu_popup_edit);
        if (user.isLogged() && TextUtils.equals(user.getUid(), String.valueOf(post.get().getAuthorId()))) {
            editPostMenuItem.setVisible(true);
        } else {
            editPostMenuItem.setVisible(false);
        }
        popup.show();
    }

    public void onReplyClick(View v) {
        rxBus.post(new QuoteEvent(String.valueOf(post.get().getPid()), String.valueOf(post.get().getPosition())));
    }

    public void onRateClick(View v) {
        rxBus.post(new RateEvent(String.valueOf(post.get().getTid()), String.valueOf(post.get().getPid())));
    }

    public void onEditClick(View v) {
        rxBus.post(new EditPostEvent(post.get(), thread.get()));
    }
}
