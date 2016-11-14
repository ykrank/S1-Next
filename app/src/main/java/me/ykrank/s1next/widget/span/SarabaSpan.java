package me.ykrank.s1next.widget.span;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.PostListGatewayActivity;

/**
 * <xmp>
 * 符合intent-filter的<a></a> tag。
 * </xmp>
 * <br>
 * <xmp>
 * eg:<a href="http://bbs.saraba1st.com/2b/forum-75-1.html" >http://bbs.saraba1st.com/2b/forum-75-1.html</a>
 * </xmp>
 * <br>
 * Created by ykrank on 2016/10/16 0016.
 */

public class SarabaSpan implements PostMovementMethod.URLSpanClick {
    /**
     * intent-filter.从AndroidManifest中提取
     */
    private static List<Pair<String, String>> HOST_FILTERS = new ArrayList<>();

    static {
        HOST_FILTERS.add(new Pair<>("bbs.saraba1st.com", ".*"));
    }

    @Override
    public boolean isMatch(Uri uri) {
        if ("http".equalsIgnoreCase(uri.getScheme())) {
            String path = uri.getEncodedPath();
            if (path == null) {
                path = "/";
            }
            for (Pair<String, String> filter : HOST_FILTERS) {
                String host = filter.first;
                String pathPattern = filter.second;

                if (host.equalsIgnoreCase(uri.getHost())
                        && path.matches(pathPattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(Uri uri, View view) {
        goSaraba(view.getContext(), uri);
    }

    // 对Saraba链接进行独立处理，调用Saraba客户端
    protected static void goSaraba(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClass(context, PostListGatewayActivity.class);
        try {
            PackageManager pm = context.getPackageManager();
            ComponentName cn = intent.resolveActivity(pm);
            if (cn == null)
                throw new ActivityNotFoundException();
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            L.e("SarabaURLSpan", "Saraba Actvity was not found for intent, " + intent.toString());
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
