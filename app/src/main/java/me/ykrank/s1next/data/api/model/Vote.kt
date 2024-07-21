package me.ykrank.s1next.data.api.model

import android.graphics.Color
import androidx.annotation.ColorInt
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.ykrank.androidtools.widget.json.JsonBooleanDeserializer
import com.github.ykrank.androidtools.widget.json.JsonBooleanSerializer
import org.jsoup.nodes.TextNode
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
    @JsonSerialize(using = JsonBooleanSerializer::class)
    @JsonDeserialize(using = JsonBooleanDeserializer::class)
    @JsonProperty("allowvote")
    var isAllow: Boolean = false

    /**
     * 最多可选项
     */
    @JsonProperty("maxchoices")
    var maxChoices: Int = 0

    /**
     * 是否允许多选
     */
    @JsonSerialize(using = JsonBooleanSerializer::class)
    @JsonDeserialize(using = JsonBooleanDeserializer::class)
    @JsonProperty("multiple")
    var isMultiple: Boolean = false

    @JsonProperty("polloptions")
    var voteOptions: Map<Int, VoteOption>? = null

    @JsonSerialize(using = Time.TimeJsonSerializer::class)
    @JsonDeserialize(using = Time.TimeJsonDeserializer::class)
    @JsonProperty("remaintime")
    var remainTime: Time? = null

    /**
     * 投票结果是否可见
     */
    @JsonSerialize(using = JsonBooleanSerializer::class)
    @JsonDeserialize(using = JsonBooleanDeserializer::class)
    @JsonProperty("visiblepoll")
    var isVisibleVote: Boolean = false

    @JsonProperty("voterscount")
    var voteCount: Int = 0

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
    data class Time(var day: Int, var hour: Int, var minute: Int, var second: Int) :
        PaperParcelable {
        override fun toString(): String =
            "Time(day=$day, hour=$hour, minute=$minute, second=$second)"

        companion object {
            @JvmField
            val CREATOR = PaperParcelVote_Time.CREATOR
        }

        class TimeJsonDeserializer : JsonDeserializer<Time>() {
            override fun deserialize(p0: JsonParser, p1: DeserializationContext?): Time {
                val time = p0.readValueAsTree<JsonNode>()
                if (time == null || !time.isArray || time.size() < 4) {
                    return Time(0, 0, 0, 0)
                }
                return Time(time[0].asInt(), time[1].asInt(), time[2].asInt(), time[3].asInt())
            }
        }

        class TimeJsonSerializer : JsonSerializer<Time?>() {
            override fun serialize(p0: Time?, p1: JsonGenerator, p2: SerializerProvider?) {
                if (p0 != null) {
                    p1.writeArray(
                        intArrayOf(
                            p0.day,
                            p0.hour,
                            p0.minute,
                            p0.second
                        ), 0, 4
                    )
                }
            }

        }
    }

    @PaperParcel
    @JsonIgnoreProperties(ignoreUnknown = true)
    class VoteOption @JsonCreator constructor(
        @JsonProperty("polloption") option: String?,
    ) : PaperParcelable {
        @JsonProperty("color")
        var color: String? = null

        @JsonProperty("percent")
        var percent: Float = 0.toFloat()

        @JsonProperty("_option")
        var option: String? = null

        @JsonProperty("polloptionid")
        var optionId: Int = 0

        @JsonProperty("votes")
        var votes: Int = 0
        val percentStr: String
            @JsonIgnore
            get() = DecimalFormat("0.00").format(percent)

        init {
            this.option = option?.let { TextNode.createFromEncoded(it).text() }
        }

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
            if (voteCount > 0) {
                this.percent = (votes * 100.0f) / voteCount
            }
        }

        companion object {
            @JvmField
            val CREATOR = PaperParcelVote_VoteOption.CREATOR
        }
    }
}
