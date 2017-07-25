package me.ykrank.s1next.data.api;

/**
 * Created by ykrank on 2016/6/17.
 */
public class ApiException extends Exception {

    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class AuthenticityTokenException extends ApiException {

        public AuthenticityTokenException(String msg) {
            super(msg);
        }

        public AuthenticityTokenException(Throwable cause) {
            super(cause);
        }

        public AuthenticityTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ApiServerException extends ApiException {

        public ApiServerException(String msg) {
            super(msg);
        }

        public ApiServerException(Throwable cause) {
            super(cause);
        }

        public ApiServerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AppServerException extends ApiException {
        private final int code;

        public AppServerException(String msg, int code) {
            super(msg);
            this.code = code;
        }
    }
}
