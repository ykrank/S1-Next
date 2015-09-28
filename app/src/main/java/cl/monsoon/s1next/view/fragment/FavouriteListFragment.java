package cl.monsoon.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.util.IntentUtil;

/**
 * A Fragment includes {@link android.support.v4.view.ViewPager}
 * to represent each page of favourite lists.
 */
public final class FavouriteListFragment extends BaseViewPagerFragment {

    public static final String TAG = FavouriteListFragment.class.getName();

    private CharSequence mTitle;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle = getText(R.string.favourites);
        if (savedInstanceState != null) {
            setTotalPages(1);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_favourites, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getContext(), Uri.parse(
                        Api.getFavouritesListUrlForBrowser(getCurrentPage() + 1)));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    BaseFragmentStatePagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        return new FavouriteListPagerAdapter(fragmentManager);
    }

    @Override
    CharSequence getTitleWithoutPosition() {
        return mTitle;
    }

    /**
     * Returns a Fragment corresponding to one of the pages of favourites.
     */
    private class FavouriteListPagerAdapter extends BaseFragmentStatePagerAdapter {

        private FavouriteListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return FavouriteListPagerFragment.newInstance(i + 1);
        }
    }
}
