package me.ykrank.s1next.data.db

import android.content.Context
import dagger.Module
import dagger.Provides
import me.ykrank.s1next.AppLife
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.biz.BlackWordBiz
import me.ykrank.s1next.data.db.biz.HistoryBiz
import me.ykrank.s1next.data.db.biz.ReadProgressBiz
import me.ykrank.s1next.data.db.biz.ThreadBiz

@Module
class DbModule {
    @Provides
    @AppLife
    fun provideAppDaoSessionManager(context: Context): AppDatabaseManager {
        return AppDatabaseManagerImpl(context)
    }

    @Provides
    @AppLife
    fun provideBlackListBiz(manager: AppDatabaseManager): BlackListBiz {
        return BlackListBiz(manager)
    }

    @Provides
    @AppLife
    fun provideBlackWordBiz(manager: AppDatabaseManager): BlackWordBiz {
        return BlackWordBiz(manager)
    }

    @Provides
    @AppLife
    fun provideReadProgressDbWrapper(manager: AppDatabaseManager): ReadProgressBiz {
        return ReadProgressBiz(manager)
    }

    @Provides
    @AppLife
    fun provideThreadBiz(manager: AppDatabaseManager): ThreadBiz {
        return ThreadBiz(manager)
    }

    @Provides
    @AppLife
    fun provideHistoryBiz(manager: AppDatabaseManager): HistoryBiz {
        return HistoryBiz(manager)
    }
}
