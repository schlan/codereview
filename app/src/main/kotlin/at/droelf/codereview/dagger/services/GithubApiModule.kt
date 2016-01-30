package at.droelf.codereview.dagger.services

import at.droelf.codereview.network.GithubApi
import at.droelf.codereview.network.GithubAuthApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class GithubApiModule {

    @Singleton
    @Provides
    fun providesGithubApi(retrofit: Retrofit): GithubApi{
        return retrofit.create(GithubApi::class.java)
    }

    @Singleton
    @Provides
    fun providesGithubAuthApi(retrofit: Retrofit): GithubAuthApi {
        return retrofit.create(GithubAuthApi::class.java)
    }

}