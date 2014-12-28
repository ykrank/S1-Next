package cl.monsoon.s1next.widget;

import android.os.RemoteException;
import android.widget.Toast;

import java.io.IOException;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Extractable;
import cl.monsoon.s1next.util.ToastHelper;

/**
 * {@code exception} is not null when load data failed.
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

    public static void handleException(Throwable exception) {
        if (exception instanceof IOException) {
            ToastHelper.showByResId(R.string.message_network_error, Toast.LENGTH_SHORT);
        } else if (exception instanceof RemoteException) {
            ToastHelper.showByResId(R.string.message_server_error, Toast.LENGTH_SHORT);
        } else {
            throw new IllegalStateException(
                    "Unhandled exception happened.");
        }
    }
}
