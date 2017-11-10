package me.ykrank.s1next.view.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.MenuItem
import com.github.ykrank.androidtools.util.LeaksUtil
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.github.ykrank.androidtools.widget.track.event.page.ActivityEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.ActivityStartEvent
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.ActivityGalleryBinding
import me.ykrank.s1next.view.fragment.GalleryFragment
import me.ykrank.s1next.view.internal.ToolbarDelegate
import javax.inject.Inject

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
class GalleryActivity : OriginActivity() {

    private lateinit var binding: ActivityGalleryBinding

    @Inject
    internal lateinit var trackAgent: DataTrackAgent

    private lateinit var imageUrls: ArrayList<String>
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityGalleryBinding>(this, R.layout.activity_gallery)
        imageUrls = intent.getStringArrayListExtra(ARG_IMAGE_URL) ?: arrayListOf()
        position = intent.getIntExtra(ARG_POSITION, 0)

        val toolbarDelegate = ToolbarDelegate(this, binding.toolbar)
        title = null
        toolbarDelegate.setupNavCrossIcon()

        binding.size = imageUrls.size
        binding.position = position

        binding.viewPager.adapter = GalleryViewPagerAdapter(supportFragmentManager, imageUrls)
        binding.viewPager.currentItem = position
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(pos: Int) {
                binding.position = pos
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        // Clean up views and other components
        super.onDestroy()
        LeaksUtil.releaseGestureBoostManagerLeaks(this)
    }

    override fun onResume() {
        super.onResume()
        trackAgent.post(ActivityStartEvent(this))
    }

    override fun onPause() {
        trackAgent.post(ActivityEndEvent(this))
        super.onPause()
    }

    companion object {
        val TAG: String = GalleryActivity::class.java.name

        private val ARG_IMAGE_URL = "image_urls"
        private val ARG_POSITION = "position"

        fun start(context: Context, imageUrls: ArrayList<String>, position: Int = 0) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putStringArrayListExtra(ARG_IMAGE_URL, imageUrls)
            intent.putExtra(ARG_POSITION, position)
            context.startActivity(intent)
        }

        fun start(context: Context, imageUrl: String?) {
            imageUrl?.let { start(context, arrayListOf(imageUrl)) }
        }
    }
}

class GalleryViewPagerAdapter(fm: FragmentManager, private val imageUrls: List<String>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return GalleryFragment.instance(imageUrls[position])
    }

    override fun getCount(): Int {
        return imageUrls.size
    }

}