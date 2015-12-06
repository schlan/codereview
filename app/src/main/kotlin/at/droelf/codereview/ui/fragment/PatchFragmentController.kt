package at.droelf.codereview.ui.fragment

import at.droelf.codereview.Constants
import at.droelf.codereview.PrettyfyHighlighter
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.patch.Patch
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable
import kotlin.text.Regex


class PatchFragmentController(val mainActivityController: MainActivityController, val githubService: GithubService) : RxHelper {

    var observable: Observable<Model.GithubDataSet>? = null

    fun data(contentUrl: String, p: String, filename: String): Observable<Model.GithubDataSet> {
        if(observable == null) {
            val patchO = Patch.parse(p)
            val contentO = githubService.fileRx(contentUrl, "").flatMap {
                PrettyfyHighlighter.highlight(it.string(), filename.split(Regex("\\.")).last())
            }

            val commentsO = githubService.commentsRx(Constants.owner, Constants.repo, Constants.pullRequest)
            val commentsReviewO = githubService.reviewCommentsRx(Constants.owner, Constants.repo, Constants.pullRequest)


            observable = Observable.combineLatest(patchO, contentO, commentsO, commentsReviewO,  { patch, fileContent, comments, reviewComments ->
                Model.GithubDataSet(patch, fileContent, comments.toList(), reviewComments.toList())
            })
                    .cache()
                    .compose(transformObservable<Model.GithubDataSet?>())
        }
        return observable!!
    }
}