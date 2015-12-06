package at.droelf.codereview.dagger.activity

import at.droelf.codereview.dagger.fragment.*
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.ui.activity.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
        modules = arrayOf(
                MainActivityModule::class
        )
)
interface MainActivityComponent{
    fun inject(mainActivity: MainActivity): MainActivity

    fun plus(userModule: UserModule): UserComponent
    fun plus(loginFragmentModule: LoginFragmentModule): LoginFragmentComponent
}