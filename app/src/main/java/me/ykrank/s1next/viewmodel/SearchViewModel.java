package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;

import me.ykrank.s1next.data.api.model.ForumSearchResult;


public final class SearchViewModel {

    public final ObservableField<ForumSearchResult> search = new ObservableField<>();
}
