package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.search.UserSearchResult;
import me.ykrank.s1next.view.activity.UserHomeActivity;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;


public final class SearchUserViewModel {

    public final ObservableField<UserSearchResult> search = new ObservableField<>();

    public void onClick(View v, View avatarView) {
        //Clear avatar false cache
        AvatarUrlsCache.clearUserAvatarCache(search.get().getUid());
        //个人主页
        UserHomeActivity.start(v.getContext(), search.get().getUid(), search.get().getName(), avatarView);
    }
}
