package me.ykrank.s1next.widget.glide

import android.util.LruCache
import com.bumptech.glide.load.Key
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import java.security.MessageDigest

/**
 * A key from string and key
 */
data class OriginalKey(private val id: String, private val signature: Key) : Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(id.toByteArray())
        signature.updateDiskCacheKey(messageDigest)
    }

    class Builder private constructor() {
        private val keyLruCache: LruCache<String, OriginalKey> =
            LruCache(ORIGIN_KEYS_MEMORY_CACHE_MAX_NUMBER)

        /**
         * obtain avatar cache key from download preference and url
         */
        fun obtainAvatarKey(manager: DownloadPreferencesManager, url: String): OriginalKey {
            var safeKey: OriginalKey?
            synchronized(keyLruCache) { safeKey = keyLruCache[url] }
            if (safeKey == null) {
                safeKey = OriginalKey(url, manager.avatarCacheInvalidationIntervalSignature)
                synchronized(keyLruCache) { keyLruCache.put(url, safeKey) }
            }
            return safeKey!!
        }

        companion object {
            val instance = Builder()
            private const val ORIGIN_KEYS_MEMORY_CACHE_MAX_NUMBER = 1000
        }
    }

    companion object {
        fun obtainAvatarKey(manager: DownloadPreferencesManager, url: String): OriginalKey {
            return Builder.instance.obtainAvatarKey(manager, url)
        }
    }
}
