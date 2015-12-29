package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.dagger.activity.FragmentScope
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.RepositoryFragment
import at.droelf.codereview.ui.fragment.RepositoryFragmentController
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Module
class RepositoryFragmentModule(private val repositoryFragment: RepositoryFragment) {

    @Provides
    @FragmentScope
    fun provideRepositoryFragment(): RepositoryFragment {
        return repositoryFragment
    }

    @Provides
    @FragmentScope
    fun provideRepositoryFragmentController(mainActivityController: MainActivityController, githubService: GithubService): RepositoryFragmentController{
        return RepositoryFragmentController(mainActivityController, githubService)
    }

}