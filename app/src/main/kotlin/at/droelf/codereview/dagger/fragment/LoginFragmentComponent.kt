package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.dagger.activity.FragmentScope
import at.droelf.codereview.ui.fragment.LoginFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(
        modules = arrayOf(
                LoginFragmentModule::class
        )
)
interface LoginFragmentComponent {
    fun inject(loginFragment: LoginFragment): LoginFragment
}