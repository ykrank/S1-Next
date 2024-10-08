package me.ykrank.s1next.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import me.ykrank.s1next.data.api.model.search.UserSearchResult
import me.ykrank.s1next.view.activity.UserHomeActivity.Companion.start

class SearchUserViewModel {
    val search = ObservableField<UserSearchResult>()
    fun onClick(v: View, avatarView: View) {
        //个人主页
        search.get()?.uid?.apply {
            start(
                (v.context as FragmentActivity),
                this,
                search.get()?.name,
                avatarView
            )
        }
    }
}
