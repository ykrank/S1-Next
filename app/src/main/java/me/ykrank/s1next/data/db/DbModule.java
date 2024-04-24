package me.ykrank.s1next.data.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.AppLife;
import me.ykrank.s1next.data.db.biz.BlackListBiz;

@Module
public class DbModule {

    @Provides
    @AppLife
    AppDatabaseManager provideAppDaoSessionManager(Context context) {
        return new AppDatabaseManagerImpl(context);
    }

    @Provides
    @AppLife
    BlackListBiz provideBlackListBiz(AppDatabaseManager manager) {
        return new BlackListBiz(manager);
    }

    @Provides
    @AppLife
    BlackWordDbWrapper provideBlackWordWDbWrapper(AppDatabaseManager manager) {
        return new BlackWordDbWrapper(appDaoSessionManager);
    }

    @Provides
    @AppLife
    ReadProgressDbWrapper provideReadProgressDbWrapper(AppDatabaseManager manager) {
        return new ReadProgressDbWrapper(appDaoSessionManager);
    }

    @Provides
    @AppLife
    ThreadDbWrapper provideThreadDbWrapperr(AppDatabaseManager manager) {
        return new ThreadDbWrapper(appDaoSessionManager);
    }

    @Provides
    @AppLife
    HistoryDbWrapper provideHistoryDbWrapper(AppDatabaseManager manager) {
        return new HistoryDbWrapper(appDaoSessionManager);
    }
}
