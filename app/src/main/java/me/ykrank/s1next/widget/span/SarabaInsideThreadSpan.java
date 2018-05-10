package me.ykrank.s1next.widget.span;

import android.net.Uri;
import android.view.View;

import com.github.ykrank.androidtools.guava.Optional;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.ThreadLink;

/**
 * <xmp>
 * eg:<a href="forum.php?mod=viewthread&tid=1340561&highlight=我" >我</a>
 * </xmp>
 * <br>
 * Created by ykrank on 2016/10/22 0016.
 */

public class SarabaInsideThreadSpan extends SarabaSpan {

    @Override
    public boolean isMatch(Uri uri) {
        if (uri.getScheme() == null && uri.getHost() == null) {
            Optional<ThreadLink> threadLink = ThreadLink.parse(uri.toString());
            if (threadLink.isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(Uri uri, View view) {
        String path = uri.toString();
        goSaraba(view.getContext(), Uri.parse(Api.BASE_URL + path));
    }
}
