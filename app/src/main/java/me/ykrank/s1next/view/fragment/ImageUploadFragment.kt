package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.ykrank.s1next.databinding.FragmentImageUploadBinding

class ImageUploadFragment :BaseFragment(){

    private lateinit var binding:FragmentImageUploadBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance():ImageUploadFragment{
            return ImageUploadFragment()
        }
    }
}