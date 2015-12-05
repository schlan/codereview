package at.droelf.codereview.dagger.application

import android.app.Application
import android.content.Context
import at.droelf.codereview.dagger.services.SquareModule
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit.Retrofit
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return app.applicationContext
    }
}