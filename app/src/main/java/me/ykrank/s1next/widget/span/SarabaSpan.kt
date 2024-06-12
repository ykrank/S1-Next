package me.ykrank.s1next.widget.span;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import androidx.core.util.Pair;

import com.github.ykrank.androidtools.util.L;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.App;
import me.ykrank.s1next.view.page.post.postlist.PostListGatewayActivity;

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

    // 对Saraba链接进行独立处理，调用Saraba客户端
    protected static void goSaraba(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClass(context, PostListGatewayActivity.class);
        try {
            PackageManager pm = context.getPackageManager();
            ActivityInfo ai = intent.resolveActivityInfo(pm, PackageManager.MATCH_DEFAULT_ONLY);
            if (ai == null) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                context.startActivity(intent);
            }
        } catch (Throwable e) {
            L.report("SarabaURLSpan startActivity error for intent, " + intent.toString(), e);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private static boolean matchSarabaInfo(Context context, Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            PackageManager pm = context.getPackageManager();
            ActivityInfo ai = intent.resolveActivityInfo(pm, PackageManager.MATCH_DEFAULT_ONLY);
            return ai != null;
        } catch (Throwable e) {
            L.d("url is not match s1");
        }
        return false;
    }

    @Override
    public boolean isMatch(Uri uri) {
        if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
            return matchSarabaInfo(App.Companion.get(), uri);
        }
        return false;
    }

    @Override
    public void onClick(Uri uri, View view) {
        goSaraba(view.getContext(), uri);
    }
}
