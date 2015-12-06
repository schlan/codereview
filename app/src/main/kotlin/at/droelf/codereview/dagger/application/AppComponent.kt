package at.droelf.codereview.dagger.application

import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.activity.MainActivityModule
import at.droelf.codereview.dagger.fragment.LoginFragmentComponent
import at.droelf.codereview.dagger.fragment.LoginFragmentModule
import at.droelf.codereview.dagger.services.DbModule
import at.droelf.codereview.dagger.services.GithubServiceModule
import at.droelf.codereview.dagger.services.SquareModule
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                SquareModule::class,
                GithubServiceModule::class,
                DbModule::class
        )
)
interface AppComponent {
    //fun plus(userModule: UserModule): UserComponent
    fun plus(mainActivityModule: MainActivityModule): MainActivityComponent
}