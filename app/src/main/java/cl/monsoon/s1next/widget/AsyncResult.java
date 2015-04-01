package cl.monsoon.s1next.widget;

import android.support.annotation.StringRes;

import java.io.IOException;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.util.ServerException;

/**
 * A Wrapper for {@link #data} and {@link #exception}.
 *
 * @param <D> the data type which can be be extracted to POJO.
 */
public final class AsyncResult<D extends Extractable> {

    public D data;

    /**
     * Either {@link cl.monsoon.s1next.util.ServerException} or {@link IOException}.
     * <p>
     * Maybe a client Network exception is the cause of the {@link ServerException}.
     */
    public Throwable exception;

    public AsyncResult() {
    }

    public AsyncResult(D data) {
        this.data = data;
    }

    @StringRes
    public int getExceptionStringRes() {
        if (exception instanceof ServerException) {
            return R.string.message_server_error;
        } else if (exception instanceof IOException) {
            return R.string.message_network_error;
        } else if (exception == null) {
            throw new IllegalStateException("Exception can't be null");
        } else {
            throw new IllegalStateException("Unknown exception.", exception);
        }
    }
}
