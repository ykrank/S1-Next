package me.ykrank.s1next.data.db;

import javax.inject.Singleton;

import dagger.Component;
import me.ykrank.s1next.AppModule;
import me.ykrank.s1next.view.fragment.HistoryListFragment;
import me.ykrank.s1next.view.fragment.PostListFragment;

@Singleton
@Component(modules = {AppModule.class, DbModule.class})
public interface DbComponent {

    BlackListDbWrapper getBlackListDbWrapper();

    ReadProgressDbWrapper getReadProgressDbWrapper();

    ThreadDbWrapper getThreadDbWrapper();

    HistoryDbWrapper getHistoryDbWrapper();

    void inject(HistoryListFragment historyListFragment);

    void inject(PostListFragment postListFragment);
}
