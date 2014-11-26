package cl.monsoon.s1next.fragment;

import android.support.v7.widget.RecyclerView;

import cl.monsoon.s1next.activity.AbsNavigationDrawerActivity;
import cl.monsoon.s1next.model.mapper.Deserialization;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * Show user info when user's cookie wasn't expired.
 */
public abstract class AbsNavigationDrawerInteractionFragment<T, D extends Deserialization, VH extends RecyclerView.ViewHolder> extends AbsRecyclerViewFragment<T, D, VH> {

    @Override
    public void onPostExecute(AsyncResult<D> dAsyncResult) {
        super.onPostExecute(dAsyncResult);

        try {
            ((AbsNavigationDrawerActivity) getActivity()).showUserInfo();
        } catch (ClassCastException e) {
            throw new IllegalStateException(
                    getActivity()
                            + " must extends AbsNavigationDrawerActivity.");
        }
    }
}
