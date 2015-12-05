package at.droelf.codereview.dagger.user

import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import dagger.Module
import dagger.Provides
import retrofit.Retrofit

@Module
class UserModule(private val data: Model.GithubAuth) {

    @Provides
    @UserScope
    fun providesGithubAuth(): Model.GithubAuth {
        return data
    }

    @Provides
    @UserScope
    fun providesGithubService(auth: Model.GithubAuth, retrofit: Retrofit): GithubService {
        return GithubService(auth, retrofit)
    }
}