package me.ykrank.s1next.view.fragment

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.ui.LibBaseViewPagerFragment
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.EventBus
import com.google.android.material.snackbar.Snackbar
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.view.event.FavoriteRemoveEvent
import javax.inject.Inject

/**
 * A Fragment includes [android.support.v4.view.ViewPager]
 * to represent each page of favourite lists.
 */
class FavouriteListFragment : BaseViewPagerFragment() {
    @Inject
    internal lateinit var mEventBus: EventBus
    @Inject
    internal lateinit var s1Service: S1Service

    private var mTitle: CharSequence? = null

    override fun getTitleWithoutPosition(): CharSequence? {
        return mTitle
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        leavePageMsg("FavouriteListFragment")

        mTitle = getText(R.string.favourites)

        mEventBus.get()
                .ofType(FavoriteRemoveEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY_VIEW))
                .subscribe { event ->
                    // reload when favorite remove
                    ApiFlatTransformer.flatMappedWithAuthenticityToken(s1Service, mUserValidator, mUser
                    ) { token -> s1Service.removeThreadFavorite(token, event.favId) }
                            .compose(RxJavaUtil.iOSingleTransformer())
                            .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY_VIEW))
                            .subscribe({ wrapper ->
                                showSnackbar(wrapper.result.message)
                                loadViewPager()
                            }, { this.onError(it) })
                }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_favourites, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_browser -> {
                IntentUtil.startViewIntentExcludeOurApp(requireContext(), Uri.parse(
                        Api.getFavouritesListUrlForBrowser(currentPage + 1)))

                return true
            }
            R.id.menu_favourites_remove -> {
                showSnackbar(R.string.how_to_remove_favourites, Snackbar.LENGTH_LONG)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter<*> {
        return FavouriteListPagerAdapter(childFragmentManager)
    }

    /**
     * Returns a Fragment corresponding to one of the pages of favourites.
     */
    private inner class FavouriteListPagerAdapter(fm: FragmentManager)
        : LibBaseViewPagerFragment.FragmentStatePagerAdapter<FavouriteListPagerFragment>(fm) {

        override fun getItem(i: Int): FavouriteListPagerFragment {
            return FavouriteListPagerFragment.newInstance(i + 1)
        }
    }

    companion object {

        val TAG = FavouriteListFragment::class.java.simpleName
    }
}
