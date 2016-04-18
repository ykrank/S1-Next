package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Favourite;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.activity.PostListActivity;
import rx.Subscription;
import rx.functions.Func1;


public final class FavouriteViewModel {

    public final ObservableField<Favourite> favourite = new ObservableField<>();

    public final ObservableField<Func1<View, Subscription>> subscription = new ObservableField<>();

    public void setSubscription(){
        subscription.set(v -> {
            Thread thread = new Thread();
            Favourite favourite = this.favourite.get();
            thread.setId(favourite.getId());
            thread.setTitle(favourite.getTitle());

            return PostListActivity.clickStartPostListActivity(v, thread);
        });
    }
}
