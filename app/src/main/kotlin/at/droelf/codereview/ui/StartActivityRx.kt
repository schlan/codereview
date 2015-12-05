package at.droelf.codereview.ui

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.utils.RxHelper
import rx.Observable

class StartActivityRx(val githubService: GithubService) : RxHelper {

    var observable: Observable<Array<GithubModel.PullRequestFile>>? = null

    fun loadData(owner: String, repo: String, pullRequest: Int): Observable<Array<GithubModel.PullRequestFile>>{
        if(observable == null){
            observable = githubService.pullRequestFilesRx(owner, repo, pullRequest)
                    .compose(transformObservable<Array<GithubModel.PullRequestFile>>())
                    .cache()
        }

        return observable!!
    }
}
