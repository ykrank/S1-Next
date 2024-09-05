package me.ykrank.s1next.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
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
class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    @Inject
    internal lateinit var trackAgent: DataTrackAgent

    private lateinit var imageUrls: ArrayList<Uri>
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        imageUrls = intent.getParcelableArrayListExtra(ARG_IMAGE_URL) ?: arrayListOf()
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

    override fun onResume() {
        super.onResume()
        trackAgent.post(ActivityStartEvent(this))
    }

    override fun onPause() {
        trackAgent.post(ActivityEndEvent(this))
        super.onPause()
    }

    companion object {
        val TAG: String = GalleryActivity::class.java.simpleName

        private const val ARG_IMAGE_URL = "image_urls"
        private const val ARG_POSITION = "position"

        fun start(context: Context, imageUrls: ArrayList<String>, position: Int = 0) {
            startUri(context, ArrayList(imageUrls.map { Uri.parse(it) }), position)
        }

        fun start(context: Context, imageUrl: String?) {
            imageUrl?.let { start(context, arrayListOf(imageUrl)) }
        }

        fun startUri(context: Context, imageUrls: ArrayList<Uri>, position: Int = 0) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putParcelableArrayListExtra(ARG_IMAGE_URL, imageUrls)
            intent.putExtra(ARG_POSITION, position)
            context.startActivity(intent)
        }

        fun startUri(context: Context, imageUrl: Uri) {
            startUri(context, arrayListOf(imageUrl))
        }
    }
}

class GalleryViewPagerAdapter(
    fm: androidx.fragment.app.FragmentManager,
    private val imageUrls: List<Uri>
) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return GalleryFragment.instance(imageUrls[position])
    }

    override fun getCount(): Int {
        return imageUrls.size
    }

}