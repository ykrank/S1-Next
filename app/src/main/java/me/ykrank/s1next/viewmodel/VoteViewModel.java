package me.ykrank.s1next.viewmodel;


import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import me.ykrank.s1next.data.api.app.model.AppVote;
import me.ykrank.s1next.data.api.model.Vote;

public final class VoteViewModel {
    @NonNull
    private final Vote vote;
    public final ObservableField<AppVote> appVote = new ObservableField<>();

    public VoteViewModel(@NonNull Vote vote) {
        this.vote = vote;
    }

    public String getVoteSummary(AppVote appVote) {
        if (appVote == null) {
            return "加载数据中...";
        }
        StringBuilder builder = new StringBuilder();
        if (appVote.isMultiple()) {
            builder.append("多选投票: ( 最多可选 ").append(appVote.getMaxChoices()).append(" 项 ) , ");
        } else {
            builder.append("单选投票, ");
        }
        if (!appVote.isVisible()) {
            builder.append("投票后结果可见, ");
        }
        if (appVote.isOvert()) {
            builder.append("公开投票, ");
        }
        builder.append(" 共有 ").append(appVote.getVoters()).append(" 人参与投票");

        return builder.toString();
    }

    public boolean isVoteable(AppVote appVote) {
        return appVote != null && vote.isAllow() && !appVote.isVoted();
    }

    public boolean isVoteable() {
        return isVoteable(appVote.get());
    }

    public boolean isMultiple() {
        return vote.isMultiple();
    }

    public View.OnClickListener clickViewAllVoter() {
        return v -> {
            
        };
    }
}
