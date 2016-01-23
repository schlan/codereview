package at.droelf.codereview.dagger.user

import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubApi
import at.droelf.codereview.network.GithubService
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
}