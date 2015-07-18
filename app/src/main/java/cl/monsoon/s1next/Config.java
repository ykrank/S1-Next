package cl.monsoon.s1next;

import java.util.concurrent.TimeUnit;

public final class Config {

    public static final int OKHTTP_CLIENT_CONNECT_TIMEOUT = 20;
    public static final int OKHTTP_CLIENT_WRITE_TIMEOUT = 20;
    public static final int OKHTTP_CLIENT_READ_TIMEOUT = 60;

    public static final long COOKIES_MAX_AGE = TimeUnit.DAYS.toSeconds(30);

    // 1MB
    public static final long AVATAR_URLS_DISK_CACHE_MAX_SIZE = 1000 * 1000;

    public static final int AVATAR_URLS_MEMORY_CACHE_MAX_NUMBER = 1000;
    public static final int AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER = 1000;

    public static final int THREADS_PER_PAGE = 50;
    public static final int POSTS_PER_PAGE = 30;

    public static final int REPLY_NOTIFICATION_MAX_LENGTH = 100;
}
