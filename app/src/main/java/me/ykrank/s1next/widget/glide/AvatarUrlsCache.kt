package me.ykrank.s1next.widget.glide

import android.util.LruCache
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.Key
import com.bumptech.glide.util.Util
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.App.Companion.get
import me.ykrank.s1next.App.Companion.preAppComponent
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.data.api.Api
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * We would get 404 status code if user hasn't set up their
 * avatar (in this case, we use the default avatar for this user).
 * So we can use this mechanism to cache these user avatars
 * (because they just use the default avatar).
 *
 *
 * According to RFC (http://tools.ietf.org/html/rfc7231#section-6.1),
 * we could cache some responses with specific status codes (like 301, 404 and 405).
 * Glide only cache the images whose response is successful.
 * But we also cache those user avatar URLs whose status code
 * is cacheable in order to tell Glide we use the default
 * avatar (error placeholder in implementation).
 *
 *
 * If this cache contains an avatar URL that Glide requests,
 * just throw an exception to tell Glide to use the error
 * placeholder for this image.
 *
 * @see AvatarStreamFetcher.loadData
 */
class AvatarUrlsCache {
    /**
     * We use both disk cache and memory cache.
     */
    private val diskLruCache: DiskLruCache
    private val lruCache: LruCache<String, Any?> = LruCache(MEMORY_CACHE_MAX_NUMBER)
    private val keyGenerator: KeyGenerator = KeyGenerator()

    init {
        val file = File(
            get().cacheDir.path
                    + File.separator + DISK_CACHE_DIRECTORY
        )
        diskLruCache = try {
            DiskLruCache.open(
                file, BuildConfig.VERSION_CODE, 1,
                DISK_CACHE_MAX_SIZE
            )
        } catch (e: IOException) {
            throw RuntimeException("Failed to open the cache in $file.", e)
        }
    }

    fun has(key: Key): Boolean {
        val encodedKey = keyGenerator.getKey(key) ?: return false
        if (lruCache[encodedKey] != null) {
            return true
        }
        try {
            synchronized(DISK_CACHE_LOCK) { return diskLruCache[encodedKey] != null }
        } catch (ignore: IOException) {
            L.report(ignore)
            return false
        }
    }

    fun put(key: Key) {
        val encodedKey = keyGenerator.getKey(key) ?: return
        lruCache.put(encodedKey, NULL_VALUE)
        try {
            synchronized(DISK_CACHE_LOCK) {
                val editor = diskLruCache.edit(encodedKey)
                // Editor will be null if there are two concurrent puts. In the worst case we will just silently fail.
                if (editor != null) {
                    try {
                        if (editor.getFile(0).createNewFile()) {
                            editor.commit()
                        }
                    } finally {
                        editor.abortUnlessCommitted()
                    }
                }
            }
        } catch (ignore: IOException) {
        }
    }

    fun remove(key: Key) {
        val encodedKey = keyGenerator.getKey(key) ?: return
        lruCache.remove(encodedKey)
        try {
            synchronized(DISK_CACHE_LOCK) { diskLruCache.remove(encodedKey) }
        } catch (ignore: IOException) {
        }
    }

    /**
     * Forked from [com.bumptech.glide.load.engine.cache.SafeKeyGenerator].
     */
    private class KeyGenerator {
        private val lruCache = LruCache<Key, String?>(AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER)
        fun getKey(key: Key): String? {
            var safeKey: String?
            synchronized(lruCache) { safeKey = lruCache[key] }
            if (safeKey == null) {
                try {
                    val messageDigest = MessageDigest.getInstance("SHA-256")
                    key.updateDiskCacheKey(messageDigest)
                    safeKey = Util.sha256BytesToHex(messageDigest.digest())
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                synchronized(lruCache) { lruCache.put(key, safeKey) }
            }
            return safeKey
        }

        companion object {
            private const val AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER = 1000
        }
    }

    companion object {
        private const val MEMORY_CACHE_MAX_NUMBER = 1000
        private const val DISK_CACHE_DIRECTORY = "avatar_urls_disk_cache"
        private const val DISK_CACHE_MAX_SIZE = (1000 * 1000 // 1MB
                ).toLong()

        /**
         * We only cache the avatar URLs as keys.
         * So we use this to represent the
         * null value because [android.util.LruCache]
         * doesn't accept null as a value.
         */
        private val NULL_VALUE = Any()
        private val DISK_CACHE_LOCK = Any()

        @JvmStatic
        fun clearUserAvatarCache(uid: String?) {
            val avatarUrlsCache = appComponent.avatarUrlsCache

            //clear avatar img error cache
            val avatarUrls = Api.getAvatarUrls(uid)
            val manager = preAppComponent.downloadPreferencesManager
            avatarUrls.forEach {
                avatarUrlsCache.remove(OriginalKey.obtainAvatarKey(manager, it))
            }
        }
    }
}
