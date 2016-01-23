package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.LoginFragment
import at.droelf.codereview.ui.fragment.LoginFragmentController
import dagger.Module
import dagger.Provides


@Module
class LoginFragmentModule(val loginFragment: LoginFragment) {

    @Provides
    @FragmentScope
    fun providesLoginFragment(): LoginFragment {
        return loginFragment
    }

    @Provides
    @FragmentScope
    fun providesLoginFragmentController(mainActivityController: MainActivityController, loginFragment: LoginFragment): LoginFragmentController {
        return LoginFragmentController(mainActivityController, loginFragment)
    }

}