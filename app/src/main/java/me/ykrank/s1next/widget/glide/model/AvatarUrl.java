package me.ykrank.s1next.widget.glide.model;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

import me.ykrank.s1next.widget.glide.AvatarStreamFetcher;

/**
 * Avatar url model to use {@link AvatarStreamFetcher}
 * Created by ykrank on 2017/3/21.
 */

public class AvatarUrl extends GlideUrl {

    public AvatarUrl(URL url) {
        super(url);
    }

    public AvatarUrl(String url) {
        super(url);
    }

    public AvatarUrl(URL url, Headers headers) {
        super(url, headers);
    }

    public AvatarUrl(String url, Headers headers) {
        super(url, headers);
    }
}
