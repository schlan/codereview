package at.droelf.codereview.dagger.user

import android.util.LruCache
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubApi
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubCacheHolder
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.storage.GithubUserStorage
import com.jakewharton.disklrucache.DiskLruCache
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Named

@Module
class UserModule(private val data: Model.GithubAuth) {

    @Provides
    @UserScope
    fun providesGithubAuth(): Model.GithubAuth {
        return data
    }

    @Provides
    @UserScope
    fun providesGithubUser(): GithubModel.User {
        return data.user
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
    fun providesGithubPersistentCache(@Named("cache_dir") cacheDir: File): DiskLruCache {
        return DiskLruCache.open(cacheDir, 1, 1,1024 * 1024 * 10)
    }

    @Provides
    @UserScope
    fun prvoidesGithubCacheHolder(githubCache: LruCache<String, Any>, diskLruCache: DiskLruCache): GithubCacheHolder {
        return GithubCacheHolder(githubCache, diskLruCache)
    }

    @Provides
    @UserScope
    fun providesGithubProviders(githubService: GithubService, githubUserStorage: GithubUserStorage, githubCacheHolder: GithubCacheHolder): GithubProvider {
        return GithubProvider(githubService, githubUserStorage, githubCacheHolder)
    }
}