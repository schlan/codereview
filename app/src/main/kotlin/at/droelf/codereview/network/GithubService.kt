package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable

class GithubService(val auth: Model.GithubAuth, val githubApi: GithubApi): GithubPagination {

    fun pullRequestFilesRx(owner: String, repo: String, number: Long): Observable<List<GithubModel.PullRequestFile>> {
        val pages = pages(githubApi.pullRequestFilesRx(owner, repo, number, token())){ githubApi.pullRequestFilesRx(owner, repo, number, token(), it)}
        return flatten(pages)
    }

    fun fileRx(url: String, accept: String): Observable<ResponseBody> {
        return githubApi.fileRx(url, accept, token()).map{
            it.body()
        }
    }

    fun commentsRx(owner: String, repo: String, number: Long): Observable<MutableList<GithubModel.Comment>> {
        return githubApi.commentsRx(owner, repo, number, token()).map {it.body()}
    }

    fun reviewCommentsRx(owner: String, repo: String, number: Long): Observable<MutableList<GithubModel.ReviewComment>> {
        return githubApi.reviewCommentsRx(owner, repo, number, token()).map {it.body()}
    }

    fun subscriptionsRx(participating: Boolean): Observable<List<GithubModel.Repository>> {
        return githubApi.subscriptionsRx(token(), participating).map {it.body()}.map { it.toList() }
    }

    fun notificationsRx(): Observable<List<GithubModel.Notification>> {
        return githubApi.notificationsRx(token()).map {it.body()}.map{ it.toList() }
    }

    fun pullRequestsRx(owner: String, repo: String): Observable<List<GithubModel.PullRequest>> {
        return githubApi.pullRequestsRx(token(), owner, repo).map {it.body()}.map{ it.toList() }
    }

    fun token() = "token ${auth.token}"

}