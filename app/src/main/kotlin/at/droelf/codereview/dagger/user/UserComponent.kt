package at.droelf.codereview.dagger.user

import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.activity.MainActivityModule
import at.droelf.codereview.dagger.activity.StartActivityComponent
import at.droelf.codereview.dagger.activity.StartActivityModule
import dagger.Subcomponent

@UserScope
@Subcomponent(
        modules = arrayOf(
                UserModule::class
        )
)
interface UserComponent {
    fun plus(mainActivityModule: MainActivityModule): MainActivityComponent
    fun plus(startActivityModule: StartActivityModule): StartActivityComponent
}