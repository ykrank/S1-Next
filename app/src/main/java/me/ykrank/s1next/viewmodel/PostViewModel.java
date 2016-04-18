package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;

import me.ykrank.s1next.data.api.model.Post;

public final class PostViewModel {

    public final ObservableField<Post> post = new ObservableField<>();
}
