package me.ykrank.s1next.viewmodel

import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import android.view.View

import me.ykrank.s1next.data.api.model.Pm
import me.ykrank.s1next.view.activity.UserHomeActivity


class PmViewModel {

    val pm = ObservableField<Pm>()

    fun onAvatarClick(v: View) {
        val uid = pm.get()?.authorId
        val name = pm.get()?.author
        if (uid != null) {
            UserHomeActivity.start(v.context as androidx.fragment.app.FragmentActivity, uid, name, v)
        }
    }
}
