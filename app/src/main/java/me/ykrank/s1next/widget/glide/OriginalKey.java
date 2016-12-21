package me.ykrank.s1next.widget.glide;

import com.bumptech.glide.load.Key;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

/**
 * Forked from {@link com.bumptech.glide.load.engine.OriginalKey}.
 */
public final class OriginalKey implements Key {

    private final String id;
    private final Key signature;

    private OriginalKey(String id, Key signature) {
        this.id = id;
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OriginalKey that = (OriginalKey) o;

        return id.equals(that.id) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + signature.hashCode();
        return result;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
        signature.updateDiskCacheKey(messageDigest);
    }

    /**
     * obtain avatar cache key from download preference and url
     */
    public static OriginalKey obtainAvatarKey(DownloadPreferencesManager manager, String url) {
        return new OriginalKey(url, manager.getAvatarCacheInvalidationIntervalSignature());
    }
}
