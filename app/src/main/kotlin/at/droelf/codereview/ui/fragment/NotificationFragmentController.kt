package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable


class NotificationFragmentController(val mainActivityController: MainActivityController, val githubProvider: GithubProvider): RxHelper {

    var observable: Observable<List<GithubModel.PullRequest>>? = null
    var listMapCache: MutableMap<String, Observable<GithubModel.PullRequestDetail>> = hashMapOf()

    fun loadPrs(): Observable<List<GithubModel.PullRequest>>{
        if (observable == null) {
            observable = githubProvider.subscriptions(false)
                    .flatMap { repos ->
                        Observable.merge(repos.map { githubProvider.pullRequests(it.owner.login, it.name) })
                    }
                    .compose(transformObservable<List<GithubModel.PullRequest>>())
                    .cache()
        }

        return observable!!
    }

    fun lazyLoadDataForPr(pr: GithubModel.PullRequest): Observable<GithubModel.PullRequestDetail> {
        val key = "${pr.id}"
        var observable = listMapCache[key]

        if(observable == null){

            observable = githubProvider.pullRequestDetail(pr.base.repo.owner.login, pr.base.repo.name, pr.number)
                    .compose(transformObservable<GithubModel.PullRequestDetail>())
                    .cache()

            listMapCache.put(key, observable)
        }

        return observable!!
    }

    fun displayFileFragment(fm: FragmentManager, owner: String, repo: String, id: Long){
        mainActivityController.displayFilesFragment(fm, owner, repo, id)
    }

}