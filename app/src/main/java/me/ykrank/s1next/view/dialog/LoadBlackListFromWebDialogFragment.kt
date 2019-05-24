package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.ui.adapter.simple.BindViewHolderCallback
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewAdapter
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.rxkotlin.Singles
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.data.api.model.WebBlackListInfo
import me.ykrank.s1next.databinding.ItemVoteBinding
import me.ykrank.s1next.databinding.DialogLoadBlacklistFromWebBinding
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.viewmodel.ItemVoteViewModel
import me.ykrank.s1next.viewmodel.VoteViewModel
import javax.inject.Inject


/**
 * A dialog lets user load website blacklist.
 */
class LoadBlackListFromWebDialogFragment : BaseDialogFragment() {
    @Inject
    lateinit var s1Service: S1Service
    @Inject
    lateinit var mUser: User

    private lateinit var binding: DialogLoadBlacklistFromWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogLoadBlacklistFromWebBinding.inflate(inflater, container, false)

        loadData()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun loadData(page:Int = 1) {
        s1Service.getWebBlackList(mUser.uid, page)
                .map { WebBlackListInfo.fromHtml(it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({
                    binding.max = it.max
                    binding.progress = it.page
                }, {
                    val activity = activity ?: return@subscribe
                    activity.toast(ErrorUtil.parse(activity, it))
                })
    }

    companion object {
        val TAG: String = LoadBlackListFromWebDialogFragment::class.java.name

        fun newInstance(): LoadBlackListFromWebDialogFragment {
            val fragment = LoadBlackListFromWebDialogFragment()

            return fragment
        }
    }
}
