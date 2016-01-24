package at.droelf.codereview.ui.fragment

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable


class RepositoryFragmentController(val mainActivityController: MainActivityController, val githubProvider: GithubProvider) : RxHelper {

    var observable: Observable<List<GithubModel.Repository>>? = null

    fun loadRepositories(): Observable<List<GithubModel.Repository>> {
        if(observable == null){
            observable = githubProvider.subscriptions(true)
                    .cache()
                    .compose(transformObservable<List<GithubModel.Repository>>())
        }
        return observable!!
    }
}