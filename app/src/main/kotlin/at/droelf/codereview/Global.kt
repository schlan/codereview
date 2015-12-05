package at.droelf.codereview

import android.app.Application
import android.content.Context
import at.droelf.codereview.dagger.*
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model

class Global: Application() {

    lateinit var appComponent: AppComponent
    private var userComponent: UserComponent? = null

    companion object Factory {
        fun get(context: Context): Global {
            return context.applicationContext as Global
        }
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    fun initDagger(){
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .squareModule(SquareModule())
                .build()
    }

    fun createUserComponent(data: Model.GithubAuth): UserComponent {
        userComponent = appComponent.plus(UserModule(data))
        return userComponent ?: throw RuntimeException("it's dead jim")
    }

    fun releaseUserComponent() {
        userComponent = null
    }

    fun userComponent(): UserComponent {
        return userComponent ?: throw RuntimeException("it's dead jim 2")
    }
}