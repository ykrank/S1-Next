package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ykrank on 2017/2/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Vote {
    /**
     * 是否拥有投票权限
     */
    @JsonIgnore
    private boolean allow;
    /**
     * 最多可选项
     */
    @JsonProperty("maxchoices")
    private int maxChoices;
    /**
     * 是否允许多选
     */
    @JsonIgnore
    private boolean multiple;
    @JsonIgnore
    private List<VoteOption> voteOptions;
    @JsonIgnore
    private Time remainTime;
    /**
     * 是否公开投票，投票结果可见
     */
    @JsonIgnore
    private boolean visibleVote;
    @JsonProperty("voterscount")
    private int voteCount;

    @JsonCreator
    public Vote(@JsonProperty("allowvote") String allowVote, @JsonProperty("multiple") String multiple,
                @JsonProperty("visiblepoll") String visiblePoll, @JsonProperty("remaintime") List<Integer> time,
                @JsonProperty("polloptions") Map<Integer, VoteOption> pollOptions) {
        this.allow = "1".equals(allowVote);
        this.multiple = "1".equals(multiple);
        this.visibleVote = "1".equals(visiblePoll);
        this.remainTime = new Time(time.get(0), time.get(1), time.get(2), time.get(3));

        List<VoteOption> options = new ArrayList<>();
        for (int i = 0; i < pollOptions.size(); i++) {
            options.add(pollOptions.get(i));
        }
        this.voteOptions = options;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public List<VoteOption> getVoteOptions() {
        return voteOptions;
    }

    public void setVoteOptions(List<VoteOption> voteOptions) {
        this.voteOptions = voteOptions;
    }

    public Time getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Time remainTime) {
        this.remainTime = remainTime;
    }

    public boolean isVisibleVote() {
        return visibleVote;
    }

    public void setVisibleVote(boolean visibleVote) {
        this.visibleVote = visibleVote;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote)) return false;
        Vote vote = (Vote) o;
        return allow == vote.allow &&
                maxChoices == vote.maxChoices &&
                multiple == vote.multiple &&
                visibleVote == vote.visibleVote &&
                voteCount == vote.voteCount &&
                Objects.equal(voteOptions, vote.voteOptions) &&
                Objects.equal(remainTime, vote.remainTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allow, maxChoices, multiple, voteOptions, remainTime, visibleVote, voteCount);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "allow=" + allow +
                ", maxChoices=" + maxChoices +
                ", multiple=" + multiple +
                ", voteOptions=" + voteOptions +
                ", remainTime=" + remainTime +
                ", visibleVote=" + visibleVote +
                ", voteCount=" + voteCount +
                '}';
    }

    public static final class Time {
        private int day;
        private int hour;
        private int minute;
        private int second;

        public Time(int day, int hour, int minute, int second) {
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Time)) return false;
            Time time = (Time) o;
            return day == time.day &&
                    hour == time.hour &&
                    minute == time.minute &&
                    second == time.second;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(day, hour, minute, second);
        }

        @Override
        public String toString() {
            return "Time{" +
                    "day=" + day +
                    ", hour=" + hour +
                    ", minute=" + minute +
                    ", second=" + second +
                    '}';
        }
    }

    public static final class VoteOption {
        @JsonProperty("color")
        private String color;
        @JsonProperty("percent")
        private float percent;
        @JsonProperty("polloption")
        private String option;
        @JsonProperty("polloptionid")
        private int optionId;
        @JsonProperty("votes")
        private int votes;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public float getPercent() {
            return percent;
        }

        public void setPercent(float percent) {
            this.percent = percent;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public int getOptionId() {
            return optionId;
        }

        public void setOptionId(int optionId) {
            this.optionId = optionId;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VoteOption)) return false;
            VoteOption that = (VoteOption) o;
            return Float.compare(that.percent, percent) == 0 &&
                    optionId == that.optionId &&
                    votes == that.votes &&
                    Objects.equal(color, that.color) &&
                    Objects.equal(option, that.option);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(color, percent, option, optionId, votes);
        }

        @Override
        public String toString() {
            return "VoteOption{" +
                    "color='" + color + '\'' +
                    ", percent=" + percent +
                    ", option='" + option + '\'' +
                    ", optionId=" + optionId +
                    ", votes=" + votes +
                    '}';
        }
    }
}
