package at.droelf.codereview.dagger.services

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

//    @Singleton @Provides
//    fun provideManager(context: Context): Manager? {
//        return null //Manager(AndroidContext(context), Manager.DEFAULT_OPTIONS)
//    }
//
//    @Singleton @Provides
//    fun provideDatabase(manager: Manager, @Named("db_name") dbName: String): Database{
//        return null //manager.getDatabase(dbName)
//    }

}