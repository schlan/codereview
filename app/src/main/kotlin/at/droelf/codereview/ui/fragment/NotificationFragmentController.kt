package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable


class NotificationFragmentController(val mainActivityController: MainActivityController, val githubService: GithubService): RxHelper {

    var observable: Observable<List<GithubModel.PullRequest>>? = null


    fun loadPrs(): Observable<List<GithubModel.PullRequest>>{
        if (observable == null) {
            observable = githubService.subscriptionsRx(false)
                    .flatMap { repos ->
                        Observable.merge(repos.map { githubService.pullRequestsRx(it.owner.login, it.name) })
                    }
                    .compose(transformObservable<List<GithubModel.PullRequest>>())
                    .cache()
        }

        return observable!!
    }

    fun displayFileFragment(fm: FragmentManager, owner: String, repo: String, id: Long){
        mainActivityController.displayFilesFragment(fm, owner, repo, id)
    }

}