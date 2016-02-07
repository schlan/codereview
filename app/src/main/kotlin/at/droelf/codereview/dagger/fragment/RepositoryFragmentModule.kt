package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.RepositoryFragment
import at.droelf.codereview.ui.fragment.RepositoryFragmentController
import dagger.Module
import dagger.Provides

@Module
class RepositoryFragmentModule(private val repositoryFragment: RepositoryFragment) {

    @Provides
    @FragmentScope
    fun provideRepositoryFragment(): RepositoryFragment {
        return repositoryFragment
    }

    @Provides
    @FragmentScope
    fun provideRepositoryFragmentController(mainActivityController: MainActivityController, githubProvider: GithubProvider, githubUserStorage: GithubUserStorage): RepositoryFragmentController{
        return RepositoryFragmentController(mainActivityController, githubProvider, githubUserStorage)
    }

}