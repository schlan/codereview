package at.droelf.codereview

import android.app.Activity
import android.support.multidex.MultiDexApplication
import at.droelf.codereview.dagger.application.AppComponent
import at.droelf.codereview.dagger.application.AppModule
import at.droelf.codereview.dagger.application.DaggerAppComponent
import at.droelf.codereview.dagger.services.DbModule
import at.droelf.codereview.dagger.services.GithubApiModule
import at.droelf.codereview.dagger.services.SquareModule
import io.realm.Realm
import io.realm.RealmConfiguration

class Global : MultiDexApplication() {

    lateinit var appComponent: AppComponent

    companion object Factory {
        fun get(activity: Activity): Global {
            return activity.application as Global
        }
    }

    override fun onCreate() {
        super.onCreate()
        initRealm()
        initDagger()
    }

    private fun initRealm() {
        val realmConfig = RealmConfiguration.Builder(this)
                .schemaVersion(0)
                .migration { dynamicRealm, ov, nv->
                    var oldVersion = ov

                    if(oldVersion == 1L){
                        oldVersion++
                    }

                    if(oldVersion == 2L){
                        oldVersion++
                    }

                    if(oldVersion < nv) {
                        throw IllegalStateException(String.format("Migration missing from v%d to v%d", oldVersion, nv))
                    }
                }
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .squareModule(SquareModule())
                .githubApiModule(GithubApiModule())
                .dbModule(DbModule())
                .build()
    }
}