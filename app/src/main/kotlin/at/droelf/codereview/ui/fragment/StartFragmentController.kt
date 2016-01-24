package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable

class StartFragmentController(val mainActivityController: MainActivityController, val githubProvider: GithubProvider) : RxHelper {

    var observable: Observable<List<GithubModel.PullRequestFile>>? = null

    fun loadData(owner: String, repo: String, pullRequest: Long): Observable<List<GithubModel.PullRequestFile>> {
        if(observable == null){
            observable = githubProvider.pullRequestFiles(owner, repo, pullRequest)
                    .compose(transformObservable<List<GithubModel.PullRequestFile>>())
                    .cache()
        }

        return observable!!
    }

    fun showFile(fm: FragmentManager, contentsUrl: String?, patch: String?, filename: String?, owner: String, repo: String, pullRequest: Long) {
        mainActivityController.showFileFragment(fm, contentsUrl, patch, filename, owner, repo, pullRequest)
    }
}
