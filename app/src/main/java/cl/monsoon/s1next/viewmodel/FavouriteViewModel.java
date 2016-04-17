package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import cl.monsoon.s1next.data.api.model.Favourite;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.view.activity.PostListActivity;
import rx.Subscription;
import rx.functions.Func1;


public final class FavouriteViewModel {

    public final ObservableField<Favourite> favourite = new ObservableField<>();

    public Func1<View, Subscription> goToThisFavourite() {
        return v -> {
            Thread thread = new Thread();
            Favourite favourite = this.favourite.get();
            thread.setId(favourite.getId());
            thread.setTitle(favourite.getTitle());
            
            return PostListActivity.clickStartPostListActivity(v, thread);
        };
    }
}
