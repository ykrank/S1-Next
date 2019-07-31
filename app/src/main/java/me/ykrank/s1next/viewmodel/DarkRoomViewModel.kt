package me.ykrank.s1next.viewmodel

import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import android.view.View

import me.ykrank.s1next.data.api.model.darkroom.DarkRoom
import me.ykrank.s1next.view.activity.UserHomeActivity


class DarkRoomViewModel {

    val darkRoom = ObservableField<DarkRoom>()

    fun onAvatarClick(v: View) {
        val uid = darkRoom.get()?.uid
        val name = darkRoom.get()?.username

        if (uid != null) {
            UserHomeActivity.start(v.context as androidx.fragment.app.FragmentActivity, uid, name, v)
        }
    }
}
