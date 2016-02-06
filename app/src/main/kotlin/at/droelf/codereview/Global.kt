package at.droelf.codereview

import android.content.Context
import android.support.multidex.MultiDexApplication
import at.droelf.codereview.dagger.application.AppComponent
import at.droelf.codereview.dagger.application.AppModule
import at.droelf.codereview.dagger.application.DaggerAppComponent
import at.droelf.codereview.dagger.services.DbModule
import at.droelf.codereview.dagger.services.GithubApiModule
import at.droelf.codereview.dagger.services.SquareModule
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary

class Global: MultiDexApplication() {

    lateinit var appComponent: AppComponent

    companion object Factory {
        fun get(context: Context): Global {
            return context.applicationContext as Global
        }
    }

    override fun onCreate() {
        super.onCreate()
        LeakCanary.install(this)
        initDagger()
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