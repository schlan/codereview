package at.droelf.codereview.ui.fragment

import at.droelf.codereview.PrettyfyHighlighter
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.patch.Patch
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable


class PatchFragmentController(val mainActivityController: MainActivityController, val githubService: GithubService) : RxHelper {

    var observable: Observable<Model.GithubDataSet>? = null

    fun data(contentUrl: String, p: String, filename: String, owner: String, repo: String, pullRequest: Long): Observable<Model.GithubDataSet> {
        if(observable == null) {
            val patchO = Patch.parse(p)
            val contentO = githubService.fileRx(contentUrl, "application/vnd.github.v3.raw").flatMap {
                PrettyfyHighlighter.highlight(it.string(), filename.split(Regex("\\.")).last())
            }

            val commentsO = githubService.commentsRx(owner, repo, pullRequest)
            val commentsReviewO = githubService.reviewCommentsRx(owner, repo, pullRequest)
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
                                Model.ReviewComment(c.id, c.body, c.user, c.position!!.toInt(), c.originalPosition.toInt(), pair.second, c.path, c.bodyHtml, c.bodyText)
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