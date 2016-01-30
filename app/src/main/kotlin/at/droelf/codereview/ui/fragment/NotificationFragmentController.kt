package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class NotificationFragmentController(val mainActivityController: MainActivityController, val githubProvider: GithubProvider): RxHelper {

    var observable: Observable<ResponseHolder<List<GithubModel.PullRequest>>>? = null
    var listMapCache: MutableMap<String, Observable<Pair<GithubModel.PullRequestDetail, List<GithubModel.Status>>>> = hashMapOf()

    var scrollPos: Int? = null

    fun loadPrs(skipCache: Boolean = false): Observable<ResponseHolder<List<GithubModel.PullRequest>>>{
        if (observable == null || skipCache) {
            observable = githubProvider.subscriptions(false, skipCache)
                    .flatMap({ repos ->
                        Observable.merge(repos.map{githubProvider.pullRequests(it.owner.login, it.name, skipCache)}, 100)
                    }, 100)
                    .compose(transformObservable<ResponseHolder<List<GithubModel.PullRequest>>>())
                    .cache()
        }

        return observable!!
    }

    fun lazyLoadDataForPr(pr: GithubModel.PullRequest): Observable<Pair<GithubModel.PullRequestDetail, List<GithubModel.Status>>> {
        val key = "${pr.id}"
        var observable = listMapCache[key]

        if(observable == null){
            val owner = pr.base.repo.owner.login
            val repo = pr.base.repo.name
            val prNumber = pr.number

            observable = Observable.combineLatest(
                    githubProvider.status(owner, repo, pr.head.ref, true),
                    githubProvider.pullRequestDetail(owner, repo, prNumber, true),
                    { a, b -> Pair(b, a)}
            )
                    .compose(transformObservable<Pair<GithubModel.PullRequestDetail, List<GithubModel.Status>>>())
                    .cache()
            listMapCache.put(key, observable)
        }

        return observable!!
    }

    fun displayFileFragment(fm: FragmentManager, owner: String, repo: String, id: Long){
        mainActivityController.displayFilesFragment(fm, owner, repo, id)
    }

}