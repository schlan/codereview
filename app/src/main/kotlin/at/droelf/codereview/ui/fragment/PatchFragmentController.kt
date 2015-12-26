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
                    .flatMap { comment ->
                        val pairPatch = comment.map { c ->
                            Observable.combineLatest(
                                    Observable.just(c),
                                    Patch.parse(c.diffHunk), { a, b ->
                                Pair(a, b)
                            })
                        }
                    Observable.concat(Observable.from(pairPatch)).toList().map { list ->
                        list.filter { it.first.position != null }
                            .map { pair ->
                                val c = pair.first
                                Model.ReviewComment(c.id, c.body, c.user, c.position!!.toInt(), c.originalPosition.toInt(), pair.second, c.path)
                        }
                    }
            }

            observable = Observable.combineLatest(patchO, contentO, commentsO, commentsReviewO,  { patch, fileContent, comments, reviewComments ->
                Model.GithubDataSet(patch, fileContent, comments.toList(), reviewComments, filename)
            })
                    .cache()
                    .compose(transformObservable<Model.GithubDataSet?>())
        }
        return observable!!
    }
}