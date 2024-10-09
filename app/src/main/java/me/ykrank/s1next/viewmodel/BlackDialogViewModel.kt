package me.ykrank.s1next.viewmodel

import android.view.View
import android.widget.TextView
import androidx.databinding.ObservableArrayList
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegateBaseImpl
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar
import me.ykrank.s1next.data.db.dbmodel.BlackList

/**
 * Created by Cintory on 2024/6/14 18:54
 * Email：Cintory@gmail.com
 */
class BlackDialogViewModel {
    val blacklist: ObservableArrayList<BlackList> = ObservableArrayList()

    val blackIdList: String
        get() {
            var idList = ""
            blacklist.forEach {
                idList += "${it.authorId} "
            }
            return idList
        }

    val blackIdName: String
        get() {
            var nameList = ""
            blacklist.forEach {
                nameList += "${it.author} "
            }
            return nameList
        }

    val blackRemark: String
        get() {
            var result = ""
            blacklist.forEach {
                result += "${it.remark} "
            }
            return result
        }

    fun clickSnackbar(): View.OnClickListener {
        return View.OnClickListener { v: View ->
            val snackbar = Snackbar.make(
                v.rootView,
                (v as TextView).text,
                Snackbar.LENGTH_SHORT
            )
            val textView = snackbar.view
                .findViewById<TextView>(R.id.snackbar_text)
            textView.maxLines = CoordinatorLayoutAnchorDelegateBaseImpl.SNACK_BAR_MAX_LINE
            snackbar.show()
        }
    }
}