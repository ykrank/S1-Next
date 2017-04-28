package me.ykrank.s1next.view.internal;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ykrank.s1next.data.api.model.Vote;
import me.ykrank.s1next.databinding.LayoutVoteBinding;
import me.ykrank.s1next.viewmodel.VoteViewModel;

/**
 * represents a delegate to vote list in post
 */

public class VoteListLayoutDelegate {
    private LayoutVoteBinding binding;

    public VoteListLayoutDelegate() {

    }

    public void onBindView(ViewGroup parent, Vote vote) {
        View child = parent.getChildAt(0);
        ViewDataBinding dataBinding = DataBindingUtil.getBinding(child);
        if (dataBinding != null && dataBinding instanceof LayoutVoteBinding) {
            binding = (LayoutVoteBinding) dataBinding;
        }
        if (vote != null && binding == null) {
            binding = LayoutVoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            binding.setModel(new VoteViewModel());
            parent.addView(binding.getRoot(), 0);
        }
        binding.getModel().vote.set(vote);
    }
}
