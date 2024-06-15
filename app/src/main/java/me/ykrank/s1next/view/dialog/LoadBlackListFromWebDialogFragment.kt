package me.ykrank.s1next.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.MainThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.RxJavaUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.WebBlackListInfo
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.DialogLoadBlacklistFromWebBinding
import me.ykrank.s1next.util.ErrorUtil
import javax.inject.Inject


/**
 * A dialog lets user load website blacklist.
 */
class LoadBlackListFromWebDialogFragment : BaseDialogFragment() {
    @Inject
    lateinit var s1Service: S1Service
    @Inject
    lateinit var mUser: User
    @Inject
    lateinit var blackListBiz: BlackListBiz

   private var callBack:(()->Unit)? = null

    private lateinit var binding: DialogLoadBlacklistFromWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogLoadBlacklistFromWebBinding.inflate(inflater, container, false)

        loadNextPage()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @MainThread
    private fun loadNextPage(page: Int = 1) {
        s1Service.getWebBlackList(mUser.uid, page)
                .map { WebBlackListInfo.fromHtml(it) }
                .map {
                    it.users.forEach { pair ->
                        blackListBiz.saveBlackList(BlackList(pair.first, pair.second, BlackList.HIDE_POST, BlackList.HIDE_FORUM))
                    }
                    it
                }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe({
                    binding.max = it.max
                    binding.progress = it.page

                    if (it.max > it.page) {
                        loadNextPage(it.page + 1)
                    }else{
                        this@LoadBlackListFromWebDialogFragment.dismiss()
                    }
                }, {
                    val activity = activity ?: return@subscribe
                    activity.toast(ErrorUtil.parse(activity, it))
                })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callBack?.invoke()
        val parentFragment = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }

    companion object {
        val TAG: String = LoadBlackListFromWebDialogFragment::class.java.name

        fun newInstance(callBack:(()->Unit)?= null): LoadBlackListFromWebDialogFragment {
            val fragment = LoadBlackListFromWebDialogFragment()
            fragment.callBack = callBack
            return fragment
        }
    }
}
