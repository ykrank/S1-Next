package me.ykrank.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.ykrank.androidautodispose.AndroidRxDispose;
import com.github.ykrank.androidlifecycle.event.FragmentEvent;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.ApiFlatTransformer;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.util.IntentUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.event.FavoriteRemoveEvent;
import me.ykrank.s1next.widget.RxBus;

/**
 * A Fragment includes {@link android.support.v4.view.ViewPager}
 * to represent each page of favourite lists.
 */
public final class FavouriteListFragment extends BaseViewPagerFragment {

    public static final String TAG = FavouriteListFragment.class.getName();

    @Inject
    RxBus mRxBus;
    @Inject
    UserValidator mUserValidator;
    @Inject
    User mUser;
    @Inject
    S1Service s1Service;

    private CharSequence mTitle;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onViewCreated(view, savedInstanceState);
        L.leaveMsg("FavouriteListFragment");

        mTitle = getText(R.string.favourites);

        mRxBus.get()
                .ofType(FavoriteRemoveEvent.class)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe(event -> {
                    // reload when favorite remove
                    ApiFlatTransformer.flatMappedWithAuthenticityToken(s1Service, mUserValidator, mUser,
                            token -> s1Service.removeThreadFavorite(token, event.getFavId()))
                            .compose(RxJavaUtil.iOTransformer())
                            .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                            .subscribe(wrapper -> {
                                showShortSnackbar(wrapper.getResult().getMessage());
                                loadViewPager();
                            }, this::onError);
                });
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
            case R.id.menu_favourites_remove:
                showLongSnackbar(R.string.how_to_remove_favourites);
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
