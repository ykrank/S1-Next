package me.ykrank.s1next.view.event

import me.ykrank.s1next.data.api.model.Vote

data class VotePostEvent(val threadId: String, val vote: Vote)