package cl.monsoon.s1next.fragment.headless;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cl.monsoon.s1next.model.Extractable;

/**
 * Used to retain data when configuration change.
 *
 * @param <D> the data type which could be extracted to POJO.
 */
public class DataRetainedFragment<D extends Extractable> extends Fragment {

    public static final String TAG = DataRetainedFragment.class.getSimpleName();

    // the data we want to retain
    private D data;

    /**
     * {@link Fragment#onCreate} method is only called once for this fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retain this fragment
        setRetainInstance(true);
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
