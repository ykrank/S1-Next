package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import cl.monsoon.s1next.data.api.model.Favourite;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.view.activity.PostListActivity;


public final class FavouriteViewModel {

    public final ObservableField<Favourite> favourite = new ObservableField<>();

    public FavouriteViewModel(Favourite Favourite) {
        this.favourite.set(Favourite);
    }

    public View.OnClickListener goToThisFavourite() {
        return v -> {
            Thread thread = new Thread();
            Favourite favourite = this.favourite.get();
            thread.setId(favourite.getId());
            thread.setTitle(favourite.getTitle());

            PostListActivity.startPostListActivity(v.getContext(), thread, false);
        };
    }
}
