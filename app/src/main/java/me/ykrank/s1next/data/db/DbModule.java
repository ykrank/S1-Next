package me.ykrank.s1next.data.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.AppLife;
import me.ykrank.s1next.BuildConfig;

@Module
public class DbModule {

    @Provides
    @AppLife
    AppDaoOpenHelper provideAppDaoOpenHelper(Context context) {
        return new AppDaoOpenHelper(context, BuildConfig.DB_NAME);
    }

    @Provides
    @AppLife
    AppDaoSessionManager provideAppDaoSessionManager(AppDaoOpenHelper helper) {
        return new AppDaoSessionManager(helper);
    }

    @Provides
    @AppLife
    BlackListDbWrapper provideBlackListDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        return new BlackListDbWrapper(appDaoSessionManager);
    }

    @Provides
    @AppLife
    ReadProgressDbWrapper provideReadProgressDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        return new ReadProgressDbWrapper(appDaoSessionManager);
    }

    @Provides
    @AppLife
    ThreadDbWrapper provideThreadDbWrapperr(AppDaoSessionManager appDaoSessionManager) {
        return new ThreadDbWrapper(appDaoSessionManager);
    }

    @Provides
    @AppLife
    HistoryDbWrapper provideHistoryDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        return new HistoryDbWrapper(appDaoSessionManager);
    }
}
