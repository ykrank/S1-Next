package me.ykrank.s1next.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import me.ykrank.s1next.view.dialog.requestdialog.PmRequestDialogFragment
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent

/**
 * A Fragment shows [EditText] to let the user pm.
 */
class NewPmFragment : BasePostFragment() {
    override var cacheKey: String? = null
        private set

    private lateinit var mToUid: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mToUid = arguments!!.getString(ARG_TO_UID)!!
        cacheKey = String.format(CACHE_KEY_PREFIX, mToUid)
        leavePageMsg("NewPmFragment##mToUid" + mToUid)
    }

    override fun onMenuSendClick(): Boolean {
        PmRequestDialogFragment.newInstance(mToUid, mReplyView.text.toString()).show(fragmentManager!!,
                PmRequestDialogFragment.TAG)

        return true
    }

    override fun isRequestDialogAccept(event: RequestDialogSuccessEvent): Boolean {
        return event.dialogFragment is PmRequestDialogFragment
    }

    companion object {

        val TAG: String = NewPmFragment::class.java.name

        private const val ARG_TO_UID = "arg_to_uid"

        private val CACHE_KEY_PREFIX = "NewPm_%s"

        fun newInstance(toUid: String): NewPmFragment {
            val fragment = NewPmFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TO_UID, toUid)
            fragment.arguments = bundle

            return fragment
        }
    }
}
