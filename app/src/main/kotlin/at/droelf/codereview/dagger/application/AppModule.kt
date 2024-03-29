package at.droelf.codereview.dagger.application

import android.app.Application
import android.content.Context
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubAuthApi
import at.droelf.codereview.network.GithubAuthService
import at.droelf.codereview.provider.GithubAuthProvider
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.utils.ReleaseTree
import dagger.Module
import dagger.Provides
import timber.log.Timber
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val app: Application, private val debug: Boolean) {

    @Singleton
    @Provides
    @Named("debug")
    fun providesDebugMode(): Boolean {
        return debug
    }

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

    @Singleton
    @Provides
    @Named("client_id")
    fun providesClientId(): String {
        return "5548f476ee88ff856ac7"
    }

    @Singleton
    @Provides
    @Named("client_secret")
    fun providesClientSecret(): String {
        return "3659193694a19a37537040258af0ebd501f62e04"
    }

    @Singleton
    @Provides
    fun providesLoggingTree(@Named("debug") debug: Boolean): Timber.Tree {
        if(debug) {
            return Timber.DebugTree()
        } else {
            return ReleaseTree()
        }
    }

    @Singleton
    @Provides
    fun providesAuthRequest(@Named("client_secret") clientSecret: String): GithubModel.AuthRequest {
        return GithubModel.AuthRequest(clientSecret, listOf("repo","user"), "Octosherlock in the house", "http://droelf.at") //TODO
    }

    @Singleton
    @Provides
    fun providesGithubAuthService(githubAuthApi: GithubAuthApi, @Named("client_id") clientId: String, authRequest: GithubModel.AuthRequest): GithubAuthService {
        return GithubAuthService(githubAuthApi, clientId, authRequest)
    }

    @Singleton
    @Provides
    fun providesGithubProvider(githubAuthService: GithubAuthService): GithubAuthProvider {
        return GithubAuthProvider(githubAuthService)
    }

    @Singleton
    @Provides
    fun providesGithubUserStorage(): GithubUserStorage {
        return GithubUserStorage()
    }
}