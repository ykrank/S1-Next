package me.ykrank.s1next.data.db;

import dagger.Component;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.view.fragment.HistoryListFragment;

@DbScope
@Component(dependencies = AppComponent.class, modules = DbModule.class)
public interface DbComponent {

    BlackListDbWrapper getBlackListDbWrapper();

    ReadProgressDbWrapper getReadProgressDbWrapper();

    ThreadDbWrapper getThreadDbWrapper();

    HistoryDbWrapper getHistoryDbWrapper();

    void inject(HistoryListFragment historyListFragment);
}
