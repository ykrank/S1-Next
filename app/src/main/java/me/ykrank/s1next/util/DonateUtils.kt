package me.ykrank.s1next.util

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.github.ykrank.androidtools.widget.AlipayDonate
import me.ykrank.s1next.R
import me.ykrank.s1next.view.dialog.AlipayDialogFragment

object DonateUtils {

    fun alipayDonate(mFragmentActivity: FragmentActivity) {
        if (AlipayDonate.hasInstalledAlipayClient(mFragmentActivity)) {
            if (AlipayDonate.startAlipayTrans(mFragmentActivity, "FKX01763C5SCSCCJIB6UE8")) {
                return
            }
        }
        if (!BuglyUtils.isPlay) {
            //For GooglePlay privacy-security
            mFragmentActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://QR.ALIPAY.COM/FKX01763C5SCSCCJIB6UE8")))
        } else {
            AlipayDialogFragment.newInstance(mFragmentActivity.getString(R.string.donate), mFragmentActivity.getString(R.string.donate_text))
                    .show(mFragmentActivity.supportFragmentManager, AlipayDialogFragment.TAG)
        }
    }
}