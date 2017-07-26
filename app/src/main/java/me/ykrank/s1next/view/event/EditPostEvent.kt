package me.ykrank.s1next.view.event

import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppThread

data class EditPostEvent(val post: AppPost, val thread: AppThread)
