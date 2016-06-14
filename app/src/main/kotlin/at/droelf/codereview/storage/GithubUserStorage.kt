package at.droelf.codereview.storage

import at.droelf.codereview.model.Model
import at.droelf.codereview.model.realm.RealmCommentPreset
import at.droelf.codereview.model.realm.RealmGithubAccount
import at.droelf.codereview.model.realm.RealmHelper
import at.droelf.codereview.model.realm.RealmRepoConfiguration
import io.realm.Realm
import timber.log.Timber
import timber.log.Timber.*
import java.sql.Wrapper
import java.util.*

class GithubUserStorage() : RealmHelper {

    fun userStored(): Boolean {
        return realmCycleOfLife {
            it.where(RealmGithubAccount::class.java).findAll().count() > 0
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
            val auth = it.where(RealmGithubAccount::class.java).findAll().firstOrNull()
            if(auth != null) accountToGithub(auth) else null
        }
    }

    fun storeRepoConfiguration(repoConfiguration: List<Model.RepoConfiguration>) {
        realmCycleOfLife {
            val storedRepos = it.where(RealmRepoConfiguration::class.java)
                    .findAll()
                    .map { repoConfigurationToGithub(it) }
            val reposToStore = repoConfiguration.filter { r -> storedRepos.find { it.id == r.id } == null }

            d("Storing the following new repo configurations: $reposToStore")

            transaction(it) {
                val repoConfig = reposToStore.map { repoConfigurationToRealm(it) }.toMutableList()
                it.copyToRealmOrUpdate(repoConfig)
            }
        }
    }

    fun getRepoConfigurations(): List<Model.RepoConfiguration> {
        return realmCycleOfLife {
            it.where(RealmRepoConfiguration::class.java)
                    .findAll()
                    .map { repoConfigurationToGithub(it) }
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
            it.where(RealmCommentPreset::class.java)
                    .findAll()
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
                    .deleteFromRealm()
            }
        }
    }
}