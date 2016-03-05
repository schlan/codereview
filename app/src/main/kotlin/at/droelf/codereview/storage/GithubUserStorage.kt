package at.droelf.codereview.storage

import at.droelf.codereview.model.Model
import at.droelf.codereview.model.realm.RealmCommentPreset
import at.droelf.codereview.model.realm.RealmGithubAccount
import at.droelf.codereview.model.realm.RealmHelper
import at.droelf.codereview.model.realm.RealmRepoConfiguration
import io.realm.Realm
import java.sql.Wrapper
import java.util.*

class GithubUserStorage() : RealmHelper {

    fun userStored(): Boolean {
        return realmCycleOfLife {
            it.allObjects(RealmGithubAccount::class.java).count() > 0
        }!!
    }

    fun storeUser(userData: Model.GithubAuth) {
        realmCycleOfLife {
            transaction(it) {
                it.copyToRealm(accountToRealm(userData))
            }
        }
    }

    fun getUserBlocking(): Model.GithubAuth? {
        return realmCycleOfLife {
            val auth = it.allObjects(RealmGithubAccount::class.java).firstOrNull()
            if(auth != null) accountToGithub(auth) else null
        }
    }

    fun storeRepoConfiguration(repoConfiguration: List<Model.RepoConfiguration>) {
        realmCycleOfLife {
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
        return realmCycleOfLife {
            it.allObjects(RealmRepoConfiguration::class.java).map { repoConfigurationToGithub(it) }
        }!!
    }

    fun getRepoConfiguration(repoId: Long): Model.RepoConfiguration {
        return realmCycleOfLife {
            val config = it.where(RealmRepoConfiguration::class.java).equalTo("id", repoId).findFirst()
            repoConfigurationToGithub(config)
        }!!
    }

    fun updateRepoConfiguration(repoId: Long, pr: Model.WatchType? = null, issue: Model.WatchType? = null) {
        realmCycleOfLife {
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

    fun getCommentPresets(): List<Model.CommentPreset> {
        return realmCycleOfLife {
            it.allObjects(RealmCommentPreset::class.java)
                    .map { commentPresetToModel(it) }
        } ?: listOf()
    }

    fun addCommentPreset(comment: String){
        realmCycleOfLife {
            transaction(it){
                val realmComment = RealmCommentPreset(Random().nextLong(), comment)
                it.copyToRealm(realmComment)
            }
        }
    }

    fun deletePreset(commentPreset: Model.CommentPreset){
        realmCycleOfLife {
            transaction(it){
                it.where(RealmCommentPreset::class.java).equalTo("id", commentPreset.id)
                    .findFirst()
                    .removeFromRealm()
            }
        }
    }

    private fun transaction(realm: Realm, update: (realm: Realm) -> Unit) {
        realm.beginTransaction()
        try {
            update(realm)
            realm.commitTransaction()
        } catch(e: Exception) {
            println("Abort realm transaction, error: ${e.message}")
            e.printStackTrace()
            realm.cancelTransaction()
        }
    }

    private fun <E> realmCycleOfLife(doStuff: (realm: Realm) -> E): E? {
        val realm = Realm.getDefaultInstance()
        val time = System.currentTimeMillis()
        var result: E? = null
        try {
            result = doStuff(realm)
        } catch (e: Exception){
            result = null
            println("Realm Error: ${e.message}")
            e.printStackTrace()
        } finally{
            realm.close()
            println("Realm was ${System.currentTimeMillis() - time}ms alive | Thread: ${Thread.currentThread().name}")
            return result
        }
    }
}