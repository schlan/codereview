package at.droelf.codereview.ui.fragment

import android.content.Context
import android.support.v4.app.FragmentManager
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable
import java.util.concurrent.TimeUnit

class StartFragmentController(val mainActivityController: MainActivityController, val githubProvider: GithubProvider) : RxHelper {

    var observable: Observable<List<Pair<GithubModel.PullRequestFile, Int>>>? = null
    var prObservable: Observable<GithubModel.PullRequestDetail>? = null
    var commentObservable: Observable<List<GithubModel.Comment>>? = null

    fun prdetails(context: Context, owner: String, repo: String, number: Long): Observable<GithubModel.PullRequestDetail> {
        if (prObservable == null) {
            prObservable = githubProvider.pullRequestDetail(owner, repo, number)
                    .delay(context.resources.getInteger(R.integer.fragment_anim_duration).toLong(), TimeUnit.MILLISECONDS)
                    .compose(transformObservable<GithubModel.PullRequestDetail>())
                    .cache()
        }
        return prObservable!!
    }

    fun comments(owner: String, repo: String, number: Long): Observable<List<GithubModel.Comment>> {
        if(commentObservable == null){
            commentObservable = githubProvider.comments(owner, repo, number)
                    .compose(transformObservable<List<GithubModel.Comment>>())
                    .cache()
        }
        return commentObservable!!
    }

    fun prfiles(owner: String, repo: String, pullRequest: Long): Observable<List<Pair<GithubModel.PullRequestFile, Int>>> {
        if (observable == null) {
            observable = Observable.combineLatest(
                    githubProvider.reviewComments(owner, repo, pullRequest),
                    githubProvider.pullRequestFiles(owner, repo, pullRequest),
                    { comments, files ->
                        files.map{ f -> Pair(f, comments.count { it -> it.path == f.filename && it.position != null }) }
                    })
                    .compose(transformObservable<List<Pair<GithubModel.PullRequestFile, Int>>>())
                    .cache()
        }
        return observable!!
    }

    fun showFile(fm: FragmentManager, contentsUrl: String?, patch: String?, filename: String?, owner: String, repo: String, pullRequest: Long) {
        mainActivityController.showFileFragment(fm, contentsUrl, patch, filename, owner, repo, pullRequest)
    }
}
