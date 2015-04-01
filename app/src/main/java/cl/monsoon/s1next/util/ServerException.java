package cl.monsoon.s1next.util;

import java.io.IOException;

/**
 * It is difficulty to distinguish whether an {@link IOException}
 * is cause by a server exception or not.
 * It's probably a server exception.
 */
@SuppressWarnings("UnusedDeclaration")
public class ServerException extends IOException {

    public ServerException() {

    }

    public ServerException(String detailMessage) {
        super(detailMessage);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}
