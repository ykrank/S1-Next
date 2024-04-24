package me.ykrank.s1next.data.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.AppLife;
import me.ykrank.s1next.data.db.biz.BlackListBiz;
import me.ykrank.s1next.data.db.biz.BlackWordBiz;
import me.ykrank.s1next.data.db.biz.HistoryBiz;

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
    BlackWordBiz provideBlackWordBiz(AppDatabaseManager manager) {
        return new BlackWordBiz(manager);
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
    HistoryBiz provideHistoryBiz(AppDatabaseManager manager) {
        return new HistoryBiz(manager);
    }
}
