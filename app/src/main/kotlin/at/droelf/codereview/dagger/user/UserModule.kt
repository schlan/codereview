package at.droelf.codereview.dagger.user

import android.util.LruCache
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubApi
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubProvider
import dagger.Module
import dagger.Provides

@Module
class UserModule(private val data: Model.GithubAuth) {

    @Provides
    @UserScope
    fun providesGithubAuth(): Model.GithubAuth {
        return data
    }

    @Provides
    @UserScope
    fun providesGithubService(auth: Model.GithubAuth, githubApi: GithubApi): GithubService {
        return GithubService(auth, githubApi)
    }

    @Provides
    @UserScope
    fun providesGithubCache(): LruCache<String, Any> {
        return LruCache(1024 * 1024 * 4)
    }

    @Provides
    @UserScope
    fun providesGithubProviders(githubService: GithubService, githubCache: LruCache<String, Any>): GithubProvider {
        return GithubProvider(githubService, githubCache)
    }
}