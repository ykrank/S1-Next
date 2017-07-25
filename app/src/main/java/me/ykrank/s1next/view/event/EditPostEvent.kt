package me.ykrank.s1next.view.event

import me.ykrank.s1next.data.api.app.AppPost
import me.ykrank.s1next.data.api.app.AppThread

data class EditPostEvent(val post: AppPost, val thread: AppThread)
