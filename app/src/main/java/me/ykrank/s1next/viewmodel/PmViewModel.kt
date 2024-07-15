package me.ykrank.s1next.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import me.ykrank.s1next.data.api.model.Pm
import me.ykrank.s1next.view.activity.UserHomeActivity


class PmViewModel(val lifecycleOwner: LifecycleOwner) {

    val pm = ObservableField<Pm>()

    fun onAvatarClick(v: View) {
        val uid = pm.get()?.authorId
        val name = pm.get()?.author
        if (uid != null) {
            UserHomeActivity.start(v.context as androidx.fragment.app.FragmentActivity, uid, name, v)
        }
    }
}
