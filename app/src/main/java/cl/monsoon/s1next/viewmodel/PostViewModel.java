package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;

import cl.monsoon.s1next.data.api.model.Post;

public final class PostViewModel {

    public final ObservableField<Post> post = new ObservableField<>();
}
