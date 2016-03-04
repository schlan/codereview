package at.droelf.codereview.ui.fragment

import android.content.Context
import at.droelf.codereview.PrettyfyHighlighter
import at.droelf.codereview.R
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.patch.Patch
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable
import java.util.concurrent.TimeUnit


class PatchFragmentController(val mainActivityController: MainActivityController, val githubProvider: GithubProvider) : RxHelper {

    var observable: Observable<Model.GithubDataSet>? = null

    fun data(context: Context, contentUrl: String, p: String, filename: String, owner: String, repo: String, pullRequest: Long): Observable<Model.GithubDataSet> {
        if(observable == null) {
            val patchO = Patch.parse(p)
            val contentO = githubProvider.file(contentUrl, "application/vnd.github.v3.raw").flatMap {
                PrettyfyHighlighter.highlight(it, filename.split(Regex("\\.")).last())
            }

            val commentsO = githubProvider.comments(owner, repo, pullRequest)
            val commentsReviewO = githubProvider.reviewComments(owner, repo, pullRequest)
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
                                Model.ReviewComment(c.id, c.body, c.user, c.position!!.toInt(), c.originalPosition.toInt(), pair.second, c.path, c.bodyHtml, c.bodyText, c.createdAt)
                        }
                    }
            }

            observable = Observable.combineLatest(patchO, contentO, commentsO, commentsReviewO,  { patch, fileContent, comments, reviewComments ->
                Model.GithubDataSet(patch, fileContent, comments.toList(), reviewComments, filename)
            })
                    .delay(context.resources.getInteger(R.integer.fragment_anim_duration).toLong(), TimeUnit.MILLISECONDS)
                    .compose(transformObservable<Model.GithubDataSet>())
                    .cache()
        }
        return observable!!
    }
}