package at.droelf.codereview.storage

import android.content.Context
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.realm.RealmGithubAccount
import at.droelf.codereview.model.realm.RealmHelper
import at.droelf.codereview.model.realm.RealmRepoConfiguration
import io.realm.Realm

class GithubUserStorage(private val context: Context) : RealmHelper {

    fun userStored(): Boolean {
        return realmOpenCloseFun {
            it.allObjects(RealmGithubAccount::class.java).count() > 0
        }
    }

    fun storeUser(userData: Model.GithubAuth) {
        return realmOpenCloseFun {
            transaction(it) {
                it.copyToRealm(accountToRealm(userData))
            }
        }
    }

    fun getUserBlocking(): Model.GithubAuth? {
        return realmOpenCloseFun {
            val auth = it.allObjects(RealmGithubAccount::class.java).firstOrNull()
            if(auth != null) accountToGithub(auth) else null
        } ?: throw RuntimeException("foorbar")
    }

    fun storeRepoConfiguration(repoConfiguration: List<Model.RepoConfiguration>) {
        realmOpenCloseFun {
            val storedRepos = it.allObjects(RealmRepoConfiguration::class.java)
                    .map { repoConfigurationToGithub(it) }
            val reposToStore = repoConfiguration.filter { r -> storedRepos.find { it.id == r.id } == null }

            println("Storing the following new repo configurations: $reposToStore")

            transaction(it) {
                val repoConfig = reposToStore.map { repoConfigurationToRealm(it) }.toMutableList()
                it.copyToRealmOrUpdate(repoConfig)
            }
        }
    }

    fun getRepoConfigurations(): List<Model.RepoConfiguration> {
        return realmOpenCloseFun {
            it.allObjects(RealmRepoConfiguration::class.java).map { repoConfigurationToGithub(it) }
        }
    }

    fun getRepoConfiguration(repoId: Long): Model.RepoConfiguration {
        return realmOpenCloseFun {
            val config = it.where(RealmRepoConfiguration::class.java).equalTo("id", repoId).findFirst()
            repoConfigurationToGithub(config)
        }
    }

    fun updateRepoConfiguration(repoId: Long, pr: Model.WatchType? = null, issue: Model.WatchType? = null) {
        realmOpenCloseFun {
            transaction(it) {
                val repoConfig = it.where(RealmRepoConfiguration::class.java).equalTo("id", repoId).findFirst()
                val config = RealmRepoConfiguration(
                        repoId,
                        pr?.id ?: repoConfig.pullRequest,
                        issue?.id ?: repoConfig.issues
                )
                it.copyToRealmOrUpdate(config)
            }
        }
    }

    private fun transaction(realm: Realm, update: (realm: Realm) -> Unit) {
        realm.beginTransaction()
        try {
            update(realm)
            realm.commitTransaction()
        } catch(e: Exception) {
            realm.cancelTransaction()
        }
    }

    private fun <E> realmOpenCloseFun(doStuff: (realm: Realm) -> E): E {
        val realm = Realm.getDefaultInstance()
        var result: E? = null
        try {
            result = doStuff(realm)
        } catch (e: Exception){
            result = null
            println("Realm Error: ${e.message}")
            e.printStackTrace()
        } finally{
            realm.close()
            return result ?: throw RuntimeException("realm error :(")
        }
    }
//
//    private fun <E> readRx(realm: Realm, read: (realm: Realm) -> E): Observable<E> {
//        return Observable.create {
//            val data = read(realm)
//            it.onNext(data)
//            it.onCompleted()
//        }
//    }
//
//    private fun transactionRx(realm: Realm, update: (realm: Realm) -> Unit): Observable<Boolean> {
//        return Observable.create({
//            realm.executeTransaction({ update(it) },
//                    object : Realm.Transaction.Callback() {
//                        override fun onSuccess() {
//                            it.onNext(true)
//                            it.onCompleted()
//                        }
//
//                        override fun onError(e: Exception?) {
//                            it.onError(e)
//                        }
//                    })
//        })
//    }
}