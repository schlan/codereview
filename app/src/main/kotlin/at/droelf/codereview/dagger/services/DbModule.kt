package at.droelf.codereview.dagger.services

import android.content.Context
import com.couchbase.lite.Database
import com.couchbase.lite.Manager
import com.couchbase.lite.android.AndroidContext
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class DbModule {

    @Singleton @Provides @Named("db_name")
    fun provideDbName(): String {
        return "codereview_db"
    }

    @Singleton @Provides
    fun provideManager(context: Context): Manager {
        return Manager(AndroidContext(context), Manager.DEFAULT_OPTIONS)
    }

    @Singleton @Provides
    fun provideDatabase(manager: Manager, @Named("db_name") dbName: String): Database{
        return manager.getDatabase(dbName)
    }

}