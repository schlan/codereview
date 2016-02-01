package at.droelf.codereview.dagger.activity

import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.activity.MainActivityController
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @ActivityScope
    @Provides
    fun provideMainActivityController(githubUserStorage: GithubUserStorage): MainActivityController{
        return MainActivityController(githubUserStorage)
    }

}


