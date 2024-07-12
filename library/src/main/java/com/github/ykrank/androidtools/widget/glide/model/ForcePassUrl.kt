package com.github.ykrank.androidtools.widget.glide.model;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

/**
 * force download url
 * Created by ykrank on 2017/3/21.
 */

public class ForcePassUrl extends GlideUrl {
    public ForcePassUrl(URL url) {
        super(url);
    }

    public ForcePassUrl(String url) {
        super(url);
    }

    public ForcePassUrl(URL url, Headers headers) {
        super(url, headers);
    }

    public ForcePassUrl(String url, Headers headers) {
        super(url, headers);
    }
}
