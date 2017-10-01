package me.ykrank.s1next.data.api.model

import android.graphics.Color
import android.support.annotation.ColorInt
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.text.DecimalFormat

/**
 * Created by ykrank on 2017/2/15.
 */
@PaperParcel
@JsonIgnoreProperties(ignoreUnknown = true)
class Vote : PaperParcelable {
    /**
     * 是否拥有投票权限
     */
    @JsonIgnore
    var isAllow: Boolean = false
    /**
     * 最多可选项
     */
    @JsonProperty("maxchoices")
    var maxChoices: Int = 0
    /**
     * 是否允许多选
     */
    @JsonIgnore
    var isMultiple: Boolean = false
    @JsonProperty("polloptions")
    var voteOptions: Map<Int, VoteOption>? = null
    @JsonIgnore
    var remainTime: Time? = null
    /**
     * 投票结果是否可见
     */
    @JsonIgnore
    var isVisibleVote: Boolean = false
    @JsonProperty("voterscount")
    var voteCount: Int = 0

    constructor()

    @JsonCreator
    constructor(@JsonProperty("allowvote") allowVote: String, @JsonProperty("multiple") multiple: String,
                @JsonProperty("visiblepoll") visiblePoll: String, @JsonProperty("remaintime") time: JsonNode?) {
        this.isAllow = "1" == allowVote
        this.isMultiple = "1" == multiple
        this.isVisibleVote = "1" == visiblePoll
        if (time != null && time.isArray) {
            this.remainTime = Time(time[0].asInt(), time[1].asInt(), time[2].asInt(), time[3].asInt())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vote

        if (isAllow != other.isAllow) return false
        if (maxChoices != other.maxChoices) return false
        if (isMultiple != other.isMultiple) return false
        if (voteOptions != other.voteOptions) return false
        if (remainTime != other.remainTime) return false
        if (isVisibleVote != other.isVisibleVote) return false
        if (voteCount != other.voteCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isAllow.hashCode()
        result = 31 * result + maxChoices
        result = 31 * result + isMultiple.hashCode()
        result = 31 * result + (voteOptions?.hashCode() ?: 0)
        result = 31 * result + (remainTime?.hashCode() ?: 0)
        result = 31 * result + isVisibleVote.hashCode()
        result = 31 * result + voteCount
        return result
    }

    override fun toString(): String =
            "Vote(isAllow=$isAllow, maxChoices=$maxChoices, isMultiple=$isMultiple, voteOptions=$voteOptions, remainTime=$remainTime, isVisibleVote=$isVisibleVote, voteCount=$voteCount)"

    companion object {
        @JvmField
        val CREATOR = PaperParcelVote.CREATOR
    }

    @PaperParcel
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Time(var day: Int, var hour: Int, var minute: Int, var second: Int) : PaperParcelable {
        override fun toString(): String =
                "Time(day=$day, hour=$hour, minute=$minute, second=$second)"

        companion object {
            @JvmField
            val CREATOR = PaperParcelVote_Time.CREATOR
        }
    }

    @PaperParcel
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VoteOption : PaperParcelable {
        @JsonProperty("color")
        var color: String? = null
        @JsonProperty("percent")
        var percent: Float = 0.toFloat()
        @JsonProperty("polloption")
        var option: String? = null
        @JsonProperty("polloptionid")
        var optionId: Int = 0
        @JsonProperty("votes")
        var votes: Int = 0
        val percentStr: String
            @JsonIgnore
            get() = DecimalFormat("0.00").format(percent)

        @ColorInt
        fun getColorInt(): Int {
            val color = this.color
            if (color != null && color.length == 6) {
                return Color.parseColor("#$color")
            }
            return Color.TRANSPARENT
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as VoteOption

            if (color != other.color) return false
            if (percent != other.percent) return false
            if (option != other.option) return false
            if (optionId != other.optionId) return false
            if (votes != other.votes) return false

            return true
        }

        override fun hashCode(): Int {
            var result = color?.hashCode() ?: 0
            result = 31 * result + percent.hashCode()
            result = 31 * result + (option?.hashCode() ?: 0)
            result = 31 * result + optionId
            result = 31 * result + votes
            return result
        }

        override fun toString(): String =
                "VoteOption(color=$color, percent=$percent, option=$option, optionId=$optionId, votes=$votes)"

        fun mergeWithAppVoteOption(other: VoteOption, voteCount: Int) {
            this.votes = other.votes
            this.percent = (votes * 100.0f) / voteCount
        }

        companion object {
            @JvmField
            val CREATOR = PaperParcelVote_VoteOption.CREATOR
        }
    }
}
