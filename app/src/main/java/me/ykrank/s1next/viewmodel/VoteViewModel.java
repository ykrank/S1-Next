package me.ykrank.s1next.viewmodel;


import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Vote;

public final class VoteViewModel {
    public final ObservableField<Vote> vote = new ObservableField<>();

    public String getVoteSummary(Vote vote) {
        StringBuilder builder = new StringBuilder();
        if (vote.isMultiple()) {
            builder.append("多选投票: ( 最多可选 ").append(vote.getMaxChoices()).append(" 项 ) , ");
        } else {
            builder.append("单选投票, ");
        }
        if (!vote.isVisibleVote()) {
            builder.append("投票后结果可见, ");
        }
        builder.append(" 共有 ").append(vote.getVoteCount()).append(" 人参与投票");

        return builder.toString();
    }

    public View.OnClickListener clickViewAllVoter(Vote vote) {
        return v -> {
            
        };
    }
}
