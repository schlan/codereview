package at.droelf.codereview.ui.activity

import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model

class MainActivityController(val mainActivity: MainActivity) {

    private var userComponent: UserComponent? = null

    fun createUserComponent(mainActivityComponent: MainActivityComponent, data: Model.GithubAuth): UserComponent {
        userComponent = mainActivityComponent.plus(UserModule(data))
        return userComponent ?: throw RuntimeException("it's dead jim")
    }

    fun releaseUserComponent() {
        userComponent = null
    }

    fun accountInstalled(): Boolean {
        return userComponent != null
    }

    fun userComponent(): UserComponent {
        return userComponent ?: throw RuntimeException("it's dead jim 2")
    }

}