package me.ykrank.s1next.data.db;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @DbScope
    BlackListDbWrapper provideBlackListDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        return new BlackListDbWrapper(appDaoSessionManager);
    }

    @Provides
    @DbScope
    ReadProgressDbWrapper provideReadProgressDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        return new ReadProgressDbWrapper(appDaoSessionManager);
    }

    @Provides
    @DbScope
    ThreadDbWrapper provideThreadDbWrapperr(AppDaoSessionManager appDaoSessionManager) {
        return new ThreadDbWrapper(appDaoSessionManager);
    }

    @Provides
    @DbScope
    HistoryDbWrapper provideHistoryDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        return new HistoryDbWrapper(appDaoSessionManager);
    }
}
