package me.ykrank.s1next.view.event

import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread

data class EditPostEvent(val post: Post, val thread: Thread)
