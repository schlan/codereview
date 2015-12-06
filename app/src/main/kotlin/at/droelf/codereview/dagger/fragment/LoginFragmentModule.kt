package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.dagger.activity.FragmentScope
import at.droelf.codereview.ui.activity.MainActivity
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
    fun providesLoginFragmentController(mainActivity: MainActivity, loginFragment: LoginFragment): LoginFragmentController {
        return LoginFragmentController(mainActivity, loginFragment)
    }

}