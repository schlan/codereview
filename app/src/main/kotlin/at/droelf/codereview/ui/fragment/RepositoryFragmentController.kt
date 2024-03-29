package at.droelf.codereview.ui.fragment

import at.droelf.codereview.model.Model
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import io.realm.Realm
import rx.Observable


class RepositoryFragmentController(
        val mainActivityController: MainActivityController,
        val githubProvider: GithubProvider,
        val githubUserStorage: GithubUserStorage) : RxHelper {

    var observable: Observable<List<Model.GithubSubscription>>? = null

    fun loadRepositories(): Observable<List<Model.GithubSubscription>> {
        return githubProvider.subscriptions(skipCache = true)
                    .compose(transformObservable<List<Model.GithubSubscription>>())
    }

    fun repositoryConfig(repoId: Long): Model.RepoConfiguration {
        return githubUserStorage.getRepoConfiguration(repoId)
    }

    fun updateRepositoryConfig(repoId: Long, pr: Model.WatchType? = null, issue: Model.WatchType? = null){
        return githubUserStorage.updateRepoConfiguration(repoId, pr, issue)
    }
}