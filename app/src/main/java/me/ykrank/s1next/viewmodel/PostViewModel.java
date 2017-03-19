package me.ykrank.s1next.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.event.QuoteEvent;
import me.ykrank.s1next.util.ActivityUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.NewRateActivity;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.view.internal.BlacklistMenuAction;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;

public final class PostViewModel {

    public final ObservableField<Post> post = new ObservableField<>();
    public final ObservableField<Thread> thread = new ObservableField<>();
    private final EventBus eventBus;

    public PostViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public CharSequence getPostCount() {
        Post p = post.get();
        if (p == null) {
            return null;
        }
        String text = "#" + p.getCount();
        // there is no need to post #1
        if ("1".equals(p.getCount())) {
            return text;
        } else {
            Spannable spannable = new SpannableString(text);
            URLSpan urlSpan = new URLSpan(StringUtils.EMPTY) {
                @Override
                public void onClick(@NonNull View widget) {
                    eventBus.post(new QuoteEvent(p.getId(), p.getCount()));
                }
            };
            spannable.setSpan(urlSpan, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
    }

    public void onAvatarClick(View v) {
        //Clear avatar false cache
        AvatarUrlsCache.clearUserAvatarCache(post.get().getAuthorId());
        //个人主页
        UserHomeActivity.start(v.getContext(), post.get().getAuthorId(), post.get().getAuthorName(), v);
    }

    public boolean onLongClick(View v) {
        //长按显示抹布菜单
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        Post postData = post.get();
        popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
            switch (menuitem.getItemId()) {
                case R.id.menu_popup_blacklist:
                    if (menuitem.getTitle().equals(v.getContext().getString(R.string.menu_blacklist_remove))) {
                        BlacklistMenuAction.removeBlacklist(eventBus, Integer.valueOf(postData.getAuthorId()), postData.getAuthorName());
                    } else {
                        Context context = ActivityUtils.getBaseContext(v.getContext());
                        if (context instanceof FragmentActivity) {
                            BlacklistMenuAction.addBlacklist((FragmentActivity) context,
                                    Integer.valueOf(postData.getAuthorId()), postData.getAuthorName());
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

    public void onReplyClick(View v) {
        eventBus.post(new QuoteEvent(post.get().getId(), post.get().getCount()));
    }

    public void onRateClick(View v) {
        if (thread.get() == null) {
            Toast.makeText(v.getContext(), R.string.invalid_thread_info, Toast.LENGTH_SHORT).show();
            return;
        }
        NewRateActivity.start(v.getContext(), thread.get().getId(), post.get().getId());
    }
}
