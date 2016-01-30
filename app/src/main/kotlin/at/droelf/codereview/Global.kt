package at.droelf.codereview

import android.app.Application
import android.content.Context
import at.droelf.codereview.dagger.application.AppComponent
import at.droelf.codereview.dagger.application.AppModule
import at.droelf.codereview.dagger.application.DaggerAppComponent
import at.droelf.codereview.dagger.services.DbModule
import at.droelf.codereview.dagger.services.GithubApiModule
import at.droelf.codereview.dagger.services.SquareModule
import com.firebase.client.Config
import com.firebase.client.Firebase
import com.firebase.client.Logger
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
        LeakCanary.install(this)
        initFirebase()
        initDagger()
    }

    private fun initFirebase(){
        Firebase.setAndroidContext(this)

        val config = Config()
        config.setLogLevel(Logger.Level.DEBUG)
        Firebase.setDefaultConfig(config)
    }

    private fun initDagger(){
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .squareModule(SquareModule())
                .githubApiModule(GithubApiModule())
                .dbModule(DbModule())
                .build()
    }
}