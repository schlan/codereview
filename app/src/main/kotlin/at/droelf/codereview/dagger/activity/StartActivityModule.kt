package at.droelf.codereview.dagger.activity

import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.StartActivity
import at.droelf.codereview.ui.StartActivityController
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Module
class StartActivityModule(private val startActivity: StartActivity) {
    @Provides
    @ActivityScope
    fun provideStartActivity(): StartActivity {
        return startActivity
    }

    @Provides
    @ActivityScope
    fun provideStartActivityController(githubService: GithubService): StartActivityController {
        return StartActivityController(githubService)
    }
}