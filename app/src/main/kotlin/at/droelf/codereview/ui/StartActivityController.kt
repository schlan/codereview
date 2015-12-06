package at.droelf.codereview.ui

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.utils.RxHelper
import rx.Observable

class StartActivityController(val githubService: GithubService) : RxHelper {

    var observable: Observable<List<GithubModel.PullRequestFile>>? = null

    fun loadData(owner: String, repo: String, pullRequest: Int): Observable<List<GithubModel.PullRequestFile>>{
        if(observable == null){
            observable = githubService.pullRequestFilesRx(owner, repo, pullRequest)
                    .compose(transformObservable<List<GithubModel.PullRequestFile>>())
                    .cache()
        }

        return observable!!
    }

    fun loadSubscriptions(){
        githubService.subscriptionsRx()
            .compose(transformObservable<List<GithubModel.Subscription>>())
            .subscribe { data ->
                val d = data
                println()
            }
    }
}
