package at.droelf.codereview.dagger.application

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return app.applicationContext
    }

    @Singleton
    @Provides
    @Named("cache_dir")
    fun providesCacheDirectory(context: Context): File {
        return File("${context.cacheDir.absolutePath}/githubcache/")
    }
}