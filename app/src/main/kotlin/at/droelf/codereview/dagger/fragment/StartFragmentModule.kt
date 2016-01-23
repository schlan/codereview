package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.StartFragment
import at.droelf.codereview.ui.fragment.StartFragmentController
import dagger.Module
import dagger.Provides

@Module
class StartFragmentModule(private val startFragment: StartFragment) {
    @Provides
    @FragmentScope
    fun provideStartFragment(): StartFragment {
        return startFragment
    }

    @Provides
    @FragmentScope
    fun provideStartFragmentController(mainActivityController: MainActivityController, githubService: GithubService): StartFragmentController {
        return StartFragmentController(mainActivityController, githubService)
    }
}