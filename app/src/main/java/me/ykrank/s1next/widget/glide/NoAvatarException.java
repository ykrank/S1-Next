package me.ykrank.s1next.widget.glide;

import java.io.IOException;

/**
 * Created by ykrank on 2017/3/6.
 */

public class NoAvatarException extends IOException {

    public NoAvatarException(String msg) {
        super(msg);
    }

    public NoAvatarException(Throwable cause) {
        super(cause);
    }

    public NoAvatarException(String message, Throwable cause) {
        super(message, cause);
    }
}
