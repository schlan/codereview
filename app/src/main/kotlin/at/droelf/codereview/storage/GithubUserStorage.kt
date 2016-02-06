package at.droelf.codereview.storage

import android.content.Context
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.realm.RealmGithubAccount
import at.droelf.codereview.model.realm.RealmHelper
import at.droelf.codereview.model.realm.RealmRepoConfiguration
import io.realm.Realm

class GithubUserStorage(private val context: Context): RealmHelper {

    private val dataKey = "github_data"

    fun userStored(): Boolean {
        return realm().allObjects(RealmGithubAccount::class.java).count() > 0
    }

    fun storeUser(userData: Model.GithubAuth) {
        transaction {
           it.copyToRealm(accountToRealm(userData))
        }
    }

    fun getUserBlocking(): Model.GithubAuth? {
        val realm = realm()
        val auth = realm.allObjects(RealmGithubAccount::class.java).firstOrNull() ?: return null
        return accountToGithub(auth)
    }

    fun storeRepoConfigration(repoConfiguration: List<Model.RepoConfiguration>){
        transaction {
            val repoConfig = repoConfiguration.map { repoConfigurationToRealm(it) }.toMutableList()
            it.copyToRealm(repoConfig)
        }
    }

    fun getRepoConfigrations(): List<Model.RepoConfiguration> {
        return realm().allObjects(RealmRepoConfiguration::class.java)
                .map{ repoConfigurationToGithub(it) }
    }

    private fun realm(): Realm {
        return Realm.getDefaultInstance()
    }

    private fun transaction(update: (realm: Realm) -> Unit){
        val realm = realm()
        realm.beginTransaction()
        update(realm)
        realm.commitTransaction()
    }
}