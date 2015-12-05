package at.droelf.codereview.dagger.activity

import at.droelf.codereview.ui.PatchActivity
import at.droelf.codereview.dagger.activity.ActivityScope
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.PatchActivityController
import dagger.Module
import dagger.Provides
import dagger.Subcomponent


@Module
class PatchActivityModule(private val patchActivity: PatchActivity) {

    @Provides
    @ActivityScope
    fun providePatchActivity(): PatchActivity {
        return patchActivity
    }

    @Provides
    @ActivityScope
    fun providePatchActivityController(githubService: GithubService): PatchActivityController{
        return PatchActivityController(githubService)
    }

}

