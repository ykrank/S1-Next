package me.ykrank.s1next.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Rate
import me.ykrank.s1next.view.fragment.RateDetailsListFragment

/**
 * Created by ykrank on 2017/1/16.
 */

class RateDetailsListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect)

        if (savedInstanceState == null) {
            val fragment = RateDetailsListFragment.instance(intent.getParcelableArrayListExtra(ARG_RATES)!!)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, fragment,
                    RateDetailsListFragment.TAG).commit()
        }
    }

    companion object {

        private const val ARG_RATES = "rates"

        fun start(context: Context, rates: ArrayList<Rate>) {
            val intent = Intent(context, RateDetailsListActivity::class.java)
            intent.putExtra(ARG_RATES, rates)
            context.startActivity(intent)
        }
    }
}
