package at.droelf.codereview.model.realm

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import io.realm.Realm
import timber.log.Timber
import java.util.*

interface RealmHelper {

    fun transaction(realm: Realm, update: (realm: Realm) -> Unit) {
        realm.beginTransaction()
        try {
            update(realm)
            realm.commitTransaction()
        } catch(e: Exception) {
            Timber.e("Abort realm transaction", e)
            realm.cancelTransaction()
        }
    }

    fun <E> realmCycleOfLife(doStuff: (realm: Realm) -> E): E? {
        val realm = Realm.getDefaultInstance()
        val time = System.currentTimeMillis()
        var result: E? = null
        try {
            result = doStuff(realm)
        } catch (e: Exception){
            result = null
            Timber.e("Error during RealmCycleOfLife", e)
        } finally{
            realm.close()
            Timber.d("Realm was ${System.currentTimeMillis() - time}ms alive | Thread: ${Thread.currentThread().name}")
            return result
        }
    }

    fun userToGithub(realmUser: RealmGithubUser): GithubModel.User{
        return GithubModel.User(
                realmUser.login!!,
                realmUser.id,
                realmUser.avatarUrl!!,
                realmUser.gravatarId!!,
                realmUser.url!!,
                realmUser.htmlUrl!!,
                realmUser.followersUrl!!,
                realmUser.followingUrl!!,
                realmUser.type!!,
                realmUser.siteAdmin!!.toBoolean()
        )
    }

    fun userToRealm(githubUser: GithubModel.User): RealmGithubUser {
        return RealmGithubUser(
                githubUser.id,
                githubUser.login,
                githubUser.avatarUrl,
                githubUser.gravatarId,
                githubUser.url,
                githubUser.htmlUrl,
                githubUser.followersUrl,
                githubUser.followingUrl,
                githubUser.type,
                githubUser.siteAdmin.toString()
        )
    }

    fun authResponseToGithub(realmAuth: RealmGithubAuth): GithubModel.AuthResponse {
        return GithubModel.AuthResponse(
                realmAuth.id,
                realmAuth.url!!,
                realmAuth.scopes!!.split("$").toMutableList(),
                realmAuth.token!!,
                realmAuth.tokenLastEight!!,
                realmAuth.hashedToken!!,
                realmAuth.updatedAt!!
        )
    }

    fun authResponseToRealm(realmAuth: GithubModel.AuthResponse): RealmGithubAuth {
        return RealmGithubAuth(
                realmAuth.id,
                realmAuth.url,
                realmAuth.scopes.reduce { a, b -> "$a$$b" },
                realmAuth.token,
                realmAuth.tokenLastEight,
                realmAuth.hashedToken,
                realmAuth.updatedAt
        )
    }

    fun accountToGithub(realmAccount: RealmGithubAccount): Model.GithubAuth {
        return Model.GithubAuth(
                authResponseToGithub(realmAccount.auth!!),
                userToGithub(realmAccount.user!!),
                realmAccount.email!!,
                UUID.fromString(realmAccount.uuid)
        )
    }

    fun accountToRealm(githubAuth: Model.GithubAuth): RealmGithubAccount {
        return RealmGithubAccount(
                githubAuth.uuid.toString(),
                authResponseToRealm(githubAuth.auth),
                userToRealm(githubAuth.user),
                githubAuth.email
        )
    }

    fun repoConfigurationToGithub(realmRepoConfiguration: RealmRepoConfiguration): Model.RepoConfiguration {
        return Model.RepoConfiguration(
                realmRepoConfiguration.id,
                Model.WatchType.fromId(realmRepoConfiguration.pullRequest!!),
                Model.WatchType.fromId(realmRepoConfiguration.issues!!)
        )
    }

    fun repoConfigurationToRealm(githubRepoConfiguration: Model.RepoConfiguration): RealmRepoConfiguration {
        return RealmRepoConfiguration(
                githubRepoConfiguration.id,
                githubRepoConfiguration.pullRequests.id,
                githubRepoConfiguration.issues.id
        )
    }

    fun commentPresetToModel(presetComment: RealmCommentPreset): Model.CommentPreset {
        return Model.CommentPreset(presetComment.id, presetComment.comment!!)
    }

    fun commentPresetToRealm(presetComment: Model.CommentPreset): RealmCommentPreset {
        return RealmCommentPreset(presetComment.id, presetComment.comment)
    }
}