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
        transaction(realm()) {
           it.copyToRealm(accountToRealm(userData))
        }
    }

    fun getUserBlocking(): Model.GithubAuth? {
        val realm = realm()
        val auth = realm.allObjects(RealmGithubAccount::class.java).firstOrNull() ?: return null
        return accountToGithub(auth)
    }

    fun storeRepoConfiguration(repoConfiguration: List<Model.RepoConfiguration>){
        val realm = realm()

        val storedRepos = realm.allObjects(RealmRepoConfiguration::class.java)
                    .map{ repoConfigurationToGithub(it) }
        val reposToStore = repoConfiguration.filter { r -> storedRepos.find { it.id == r.id } == null }

        println("Storing the following new repo configurations: $reposToStore")

        transaction(realm) {
            val repoConfig = reposToStore.map { repoConfigurationToRealm(it) }.toMutableList()
            it.copyToRealmOrUpdate(repoConfig)
        }
    }

    fun getRepoConfigurations(): List<Model.RepoConfiguration> {
        return realm().allObjects(RealmRepoConfiguration::class.java)
                .map{ repoConfigurationToGithub(it) }
    }

    fun getRepoConfiguration(realm: Realm, repoId: Long): Model.RepoConfiguration {
        val config = realm.where(RealmRepoConfiguration::class.java).equalTo("id", repoId).findFirst()
        return repoConfigurationToGithub(config)
    }

    fun updateRepoConfiguration(realm: Realm, repoId: Long, pr: Model.WatchType? = null, issue: Model.WatchType? = null) {
        transaction(realm) {
            val repoConfig = it.where(RealmRepoConfiguration::class.java).equalTo("id", repoId).findFirst()
            val config = RealmRepoConfiguration(
                    repoId,
                    pr?.id ?: repoConfig.pullRequest,
                    issue?.id ?: repoConfig.issues
            )
            realm.copyToRealmOrUpdate(config)
        }
    }

    private fun realm(): Realm {
        return Realm.getDefaultInstance()
    }

    private fun transaction(realm: Realm, update: (realm: Realm) -> Unit){
        realm.beginTransaction()
        update(realm)
        realm.commitTransaction()
    }
}