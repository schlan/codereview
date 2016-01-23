package at.droelf.codereview.dagger.services

import at.droelf.codereview.network.GithubApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class GithubServiceModule {

    @Singleton
    @Provides
    fun providesGithubApi(retrofit: Retrofit): GithubApi{
        return retrofit.create(GithubApi::class.java)
    }
}