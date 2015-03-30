package cl.monsoon.s1next.widget;

import android.os.RemoteException;
import android.support.annotation.StringRes;

import java.io.IOException;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Extractable;

/**
 * A Wrapper for {@link #data} and {@link #exception}.
 *
 * @param <D> the data type which can be be extracted to POJO.
 */
public final class AsyncResult<D extends Extractable> {

    public D data;
    public Throwable exception;

    public AsyncResult() {
    }

    public AsyncResult(D data) {
        this.data = data;
    }

    @StringRes
    public int getExceptionString() {
        if (exception instanceof RemoteException) {
            return R.string.message_server_error;
        } else if (exception instanceof IOException) {
            return R.string.message_network_error;
        } else if (exception == null) {
            throw new IllegalStateException("Exception can't be null");
        } else {
            throw new IllegalStateException("Unknown exception happened.", exception);
        }
    }
}
