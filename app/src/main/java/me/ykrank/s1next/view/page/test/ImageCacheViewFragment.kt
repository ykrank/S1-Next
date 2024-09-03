package me.ykrank.s1next.view.page.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.databinding.FragmentBaseBinding
import java.io.File

/**
 * Created by ykrank on 9/3/24
 */
class ImageCacheViewFragment : Fragment() {

    private lateinit var binding: FragmentBaseBinding
    private var adapter = ImageCacheViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseBinding.inflate(inflater)
        binding.root.setBackgroundResource(com.github.ykrank.androidtools.R.color.saraba_primary)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                Glide.getPhotoCacheDir(requireContext())?.listFiles()?.toList() ?: emptyList<File>()
            }
            adapter.updateData(list)
        }
    }

    companion object {
        const val TAG = "ImageCacheViewFragment"
    }
}