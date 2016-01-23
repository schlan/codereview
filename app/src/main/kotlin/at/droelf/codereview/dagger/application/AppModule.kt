package at.droelf.codereview.dagger.application

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return app.applicationContext
    }
}