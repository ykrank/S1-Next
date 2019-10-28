package me.ykrank.s1next.view.fragment

import android.content.Context
import androidx.annotation.CallSuper
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.github.ykrank.androidtools.ui.LibBaseViewPagerFragment
import com.github.ykrank.androidtools.util.ResourceUtil
import com.github.ykrank.androidtools.util.StringUtil
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.UserValidator
import javax.inject.Inject

/**
 * A base Fragment wraps [ViewPager] and provides related methods.
 */
abstract class BaseViewPagerFragment : LibBaseViewPagerFragment() {
    @Inject
    internal lateinit var mUserValidator: UserValidator
    @Inject
    internal lateinit var trackAgent: DataTrackAgent
    @Inject
    internal lateinit var mUser: User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        App.appComponent.inject(this)
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_view_pager, menu)

    }

    override fun findMenuPageJump(menu: Menu): MenuItem? {
        return menu.findItem(R.id.menu_page_jump)
    }


    override fun setTitleWithPosition(position: Int) {
        val titleWithoutPosition = getTitleWithoutPosition()
        if (titleWithoutPosition == null) {
            activity?.title = null
            return
        }

        val titleWithPosition: String
        if (ResourceUtil.isRTL(resources)) {
            titleWithPosition = StringUtil.concatWithTwoSpaces(position + 1, titleWithoutPosition)
        } else {
            titleWithPosition = StringUtil.concatWithTwoSpaces(titleWithoutPosition, position + 1)
        }
        activity?.title = titleWithPosition
    }
}
