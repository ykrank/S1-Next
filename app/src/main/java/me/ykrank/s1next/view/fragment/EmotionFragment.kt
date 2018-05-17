package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.ykrank.s1next.databinding.FragmentEmotionBinding
import me.ykrank.s1next.view.adapter.EmoticonPagerAdapter

class EmotionFragment : BaseFragment() {

    private lateinit var binding: FragmentEmotionBinding

    protected lateinit var mEmoticonKeyboard: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEmotionBinding.inflate(inflater, container, false)

        mEmoticonKeyboard = binding.emoticonKeyboard

        setupEmoticonPager()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun setupEmoticonPager() {
        val viewPager = binding.emoticonKeyboardPager
        viewPager.adapter = EmoticonPagerAdapter(activity)

        val tabLayout = binding.emoticonKeyboardTabLayout
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        fun newInstance(): EmotionFragment {
            return EmotionFragment()
        }
    }
}