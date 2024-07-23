package me.ykrank.s1next.data.db

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import me.ykrank.s1next.AppLife
import me.ykrank.s1next.data.cache.CacheDatabaseManager
import me.ykrank.s1next.data.cache.CacheDatabaseManagerImpl
import me.ykrank.s1next.data.cache.biz.CacheBiz
import me.ykrank.s1next.data.cache.biz.CacheGroupBiz
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.biz.BlackWordBiz
import me.ykrank.s1next.data.db.biz.HistoryBiz
import me.ykrank.s1next.data.db.biz.LoginUserBiz
import me.ykrank.s1next.data.db.biz.ReadProgressBiz
import me.ykrank.s1next.data.db.biz.ThreadBiz
import me.ykrank.s1next.widget.encrypt.AndroidStoreEncryption
import me.ykrank.s1next.widget.encrypt.Encryption

@Module
class DbModule {
    @Provides
    @AppLife
    fun provideAppDatabaseManager(context: Context): AppDatabaseManager {
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

    @Provides
    @AppLife
    fun provideLoginUserBiz(manager: AppDatabaseManager, encryption: Encryption): LoginUserBiz {
        return LoginUserBiz(manager, encryption)
    }


    @Provides
    @AppLife
    fun provideDbEncryption(): Encryption {
        return AndroidStoreEncryption("s1next_db")
    }

    @Provides
    @AppLife
    fun provideCacheDatabaseManager(
        context: Context,
        appManager: AppDatabaseManager
    ): CacheDatabaseManager {
        return CacheDatabaseManagerImpl(context, appManager)
    }

    @Provides
    @AppLife
    fun provideCacheBiz(manager: CacheDatabaseManager, objectMapper: ObjectMapper): CacheBiz {
        return CacheBiz(manager, objectMapper)
    }

    @Provides
    @AppLife
    fun provideCacheGroupBiz(manager: CacheDatabaseManager): CacheGroupBiz {
        return CacheGroupBiz(manager)
    }
}
