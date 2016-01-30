package at.droelf.codereview.dagger.application

import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.activity.MainActivityModule
import at.droelf.codereview.dagger.services.DbModule
import at.droelf.codereview.dagger.services.GithubApiModule
import at.droelf.codereview.dagger.services.SquareModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AppModule::class,
                SquareModule::class,
                GithubApiModule::class,
                DbModule::class
        )
)
interface AppComponent {
    //fun plus(userModule: UserModule): UserComponent
    fun plus(mainActivityModule: MainActivityModule): MainActivityComponent
}