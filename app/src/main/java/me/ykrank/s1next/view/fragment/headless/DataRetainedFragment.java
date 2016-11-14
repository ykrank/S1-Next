package me.ykrank.s1next.view.fragment.headless;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Used to retain data when configuration changes.
 * <p>
 * see https://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject
 *
 * @param <D> The data we want to retain when configuration changes.
 */
public class DataRetainedFragment<D> extends Fragment {

    public static final String TAG = DataRetainedFragment.class.getName();

    public D data;

    /**
     * {@code stale} is false if this {@link DataRetainedFragment} was created
     * for the first time or killed by system, otherwise true.
     */
    public boolean stale;
    /**
     * id to judge whether data is valid
     */
    public String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retain this fragment
        setRetainInstance(true);
    }
}
