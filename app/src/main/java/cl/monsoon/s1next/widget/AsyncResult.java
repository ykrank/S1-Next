package cl.monsoon.s1next.widget;

import android.os.RemoteException;

import java.io.IOException;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.util.ToastHelper;

/**
 * {@code exception} is not null when load data failed.
 *
 * @param <D> the data type which can be deserialized from JSON.
 */
public final class AsyncResult<D extends Deserialization> {

    public D data;
    public Throwable exception;

    public AsyncResult() {
    }

    public AsyncResult(D data) {
        this.data = data;
    }

    public static void handleException(Throwable exception) {
        if (exception instanceof IOException) {
            ToastHelper.showByResId(R.string.message_network_error);
        } else if (exception instanceof RemoteException) {
            ToastHelper.showByResId(R.string.message_server_error);
        } else {
            throw new IllegalStateException(
                    "Unhandled exception happened.");
        }
    }
}
