package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import me.ykrank.s1next.data.api.model.Vote;

/**
 * Created by ykrank on 2017/9/29.
 */

public class ItemVoteViewModel {
    @NonNull
    private final VoteViewModel voteVM;
    public final ObservableField<Vote.VoteOption> option = new ObservableField<>();

    public ItemVoteViewModel(@NonNull VoteViewModel voteVM) {
        this.voteVM = voteVM;
    }

    public boolean isSingleVotable() {
        return voteVM.isVoteable() && !voteVM.isMultiple();
    }

    public boolean isMultiVotable() {
        return voteVM.isVoteable() && voteVM.isMultiple();
    }
}
