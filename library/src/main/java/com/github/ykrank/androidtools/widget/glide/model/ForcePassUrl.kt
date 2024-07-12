package com.github.ykrank.androidtools.widget.glide.model

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import java.net.URL

/**
 * force download url.
 * Created by ykrank on 2017/3/21.
 */
class ForcePassUrl : GlideUrl {
    constructor(url: URL?) : super(url)
    constructor(url: String?) : super(url)
    constructor(url: URL?, headers: Headers?) : super(url, headers)
    constructor(url: String?, headers: Headers?) : super(url, headers)
}
