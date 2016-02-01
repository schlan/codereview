package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.provider.GithubAuthProvider
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.LoginFragmentController
import dagger.Module
import dagger.Provides


@Module
class LoginFragmentModule {

    @Provides
    @FragmentScope
    fun providesLoginFragmentController(mainActivityController: MainActivityController, githubAuthProvider: GithubAuthProvider, githubUserStorage: GithubUserStorage): LoginFragmentController {
        return LoginFragmentController(mainActivityController, githubAuthProvider, githubUserStorage)
    }

}