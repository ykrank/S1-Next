package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Favourite;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.event.FavoriteRemoveEvent;
import me.ykrank.s1next.view.activity.PostListActivity;
import me.ykrank.s1next.widget.EventBus;
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

    public View.OnLongClickListener removeFromFavourites(final EventBus eventBus) {
        return v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.setOnMenuItemClickListener((MenuItem menuitem) -> {
                switch (menuitem.getItemId()) {
                    case R.id.menu_popup_remove_favourite:
                        eventBus.post(new FavoriteRemoveEvent(favourite.get().getFavId()));
                        return true;
                    default:
                        return false;
                }
            });
            popup.inflate(R.menu.popup_favorites);
            popup.show();
            return true;
        };
    }
}
