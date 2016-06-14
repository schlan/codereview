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
import timber.log.Timber
import javax.inject.Inject

class Global : MultiDexApplication() {

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var timberTree: Timber.Tree

    companion object Factory {
        fun get(activity: Activity): Global {
            return activity.application as Global
        }
    }

    override fun onCreate() {
        super.onCreate()
        initDagger(debug = false)
        Timber.d("Dagger initialized")

        Timber.plant(timberTree)

        initRealm()
        Timber.d("Realm initialized")
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

    private fun initDagger(debug: Boolean) {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this, debug))
                .squareModule(SquareModule())
                .githubApiModule(GithubApiModule())
                .dbModule(DbModule())
                .build()
        appComponent.inject(this)
    }
}