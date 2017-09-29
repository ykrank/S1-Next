package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import me.ykrank.s1next.data.api.model.Vote;

/**
 * Created by ykrank on 2017/9/29.
 */

public class ItemVoteViewModel {
    @NonNull
    private final VoteViewModel voteVM;
    @NonNull
    public final Vote.VoteOption option;
    public final ObservableBoolean selected = new ObservableBoolean();

    public ItemVoteViewModel(@NonNull VoteViewModel voteVM, @NonNull Vote.VoteOption option) {
        this.voteVM = voteVM;
        this.option = option;
    }

    public boolean isSingleVotable() {
        return voteVM.isVoteable() && !voteVM.isMultiple();
    }

    public boolean isMultiVotable() {
        return voteVM.isVoteable() && voteVM.isMultiple();
    }
}
