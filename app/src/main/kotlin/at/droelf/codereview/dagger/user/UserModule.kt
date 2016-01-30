package at.droelf.codereview.dagger.user

import android.util.LruCache
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubApi
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubProvider
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
    fun providesGithubPersistantCache(@Named("cache_dir") cacheDir: File): DiskLruCache {
        return DiskLruCache.open(cacheDir, 1, 1,1024 * 1024 * 10)
    }

    @Provides
    @UserScope
    fun providesGithubProviders(githubService: GithubService, githubCache: LruCache<String, Any>, diskLruCache: DiskLruCache): GithubProvider {
        return GithubProvider(githubService, githubCache, diskLruCache)
    }
}