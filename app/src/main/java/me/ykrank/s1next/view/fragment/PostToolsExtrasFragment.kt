package me.ykrank.s1next.view.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import me.ykrank.s1next.R
import me.ykrank.s1next.databinding.FragmentPostToolsExtrasBinding
import me.ykrank.s1next.databinding.ItemPostToolsExtrasBinding
import me.ykrank.s1next.view.internal.*

class PostToolsExtrasFragment : BaseFragment() {

    private lateinit var binding: FragmentPostToolsExtrasBinding

    private lateinit var provider: PostToolsExtrasContextProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        provider = parentFragment as PostToolsExtrasContextProvider
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostToolsExtrasBinding.inflate(inflater, container, false)

        binding.recycleView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 5, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        val adapter = SimpleRecycleViewAdapter(context!!, R.layout.item_post_tools_extras, false, createViewHolderCallback = {
            val bind = it as ItemPostToolsExtrasBinding
            bind.root.setOnClickListener {
                bind.model?.onClick(provider.currentEditText)
            }
        })
        adapter.swapDataSet(listOf(PostToolsExtraBold(), PostToolsExtraItalic(), PostToolsExtraUnderline(),
                PostToolsExtraLink(), PostToolsExtraStrikethrough()))
        binding.recycleView.adapter = adapter

        return binding.root
    }

    interface PostToolsExtrasContextProvider {
        val currentEditText: EditText
    }

    companion object {
        val TAG: String = PostToolsExtrasFragment::class.java.name

        fun newInstance(): PostToolsExtrasFragment {
            return PostToolsExtrasFragment()
        }
    }
}