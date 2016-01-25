package at.droelf.codereview

import android.app.Application
import android.content.Context
import at.droelf.codereview.dagger.*
import at.droelf.codereview.dagger.application.AppComponent
import at.droelf.codereview.dagger.application.AppModule
import at.droelf.codereview.dagger.application.DaggerAppComponent
import at.droelf.codereview.dagger.services.DbModule
import at.droelf.codereview.dagger.services.GithubServiceModule
import at.droelf.codereview.dagger.services.SquareModule
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model
import com.squareup.leakcanary.LeakCanary

class Global: Application() {

    lateinit var appComponent: AppComponent

    companion object Factory {
        fun get(context: Context): Global {
            return context.applicationContext as Global
        }
    }

    override fun onCreate() {
        super.onCreate()
        LeakCanary.install(this);
        initDagger()
    }

    fun initDagger(){
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .squareModule(SquareModule())
                .githubServiceModule(GithubServiceModule())
                .dbModule(DbModule())
                .build()
    }
}