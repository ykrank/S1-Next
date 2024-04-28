package me.ykrank.s1next.viewmodel;

import android.net.Uri;
import android.view.View;

import androidx.databinding.ObservableField;

import me.ykrank.s1next.data.api.model.HomeThread;
import me.ykrank.s1next.view.page.post.postlist.PostListGatewayActivity;

/**
 * Created by ykrank on 2017/2/4.
 */

public final class HomeReplyTitleViewModel {
    public final ObservableField<HomeThread> thread = new ObservableField<>();

    public final void onClick(View v) {
        PostListGatewayActivity.Companion.start(v.getContext(), Uri.parse(thread.get().getUrl()));
    }
}
