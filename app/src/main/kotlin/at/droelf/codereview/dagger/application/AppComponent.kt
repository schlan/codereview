package at.droelf.codereview.dagger.application

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
                GithubServiceModule::class
        )
)
interface AppComponent {
    fun plus(userModule: UserModule): UserComponent
}