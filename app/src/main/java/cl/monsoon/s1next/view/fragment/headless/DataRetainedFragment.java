package cl.monsoon.s1next.view.fragment.headless;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Used to retain data when configuration change.
 *
 * @param <D> the data type which could be extracted to POJO.
 */
public class DataRetainedFragment<D> extends Fragment {

    public static final String TAG = DataRetainedFragment.class.getName();

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
