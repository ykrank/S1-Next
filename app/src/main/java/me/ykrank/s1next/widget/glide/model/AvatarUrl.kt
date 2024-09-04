package me.ykrank.s1next.widget.glide.model

import com.bumptech.glide.load.model.GlideUrl

/**
 * Avatar url model to use [AvatarStreamFetcher]
 * Created by ykrank on 2017/3/21.
 */
class AvatarUrl(url: String?, val forcePass: Boolean = false) : GlideUrl(url) {
}
