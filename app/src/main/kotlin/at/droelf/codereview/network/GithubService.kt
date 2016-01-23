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
        return pages(githubApi.pullRequestFilesRx(owner, repo, number, token())){
            githubApi.pullRequestFilesRx(owner, repo, number, token(), it)
        }.flatten()
    }

    fun fileRx(url: String, accept: String): Observable<ResponseBody> {
        return githubApi.fileRx(url, accept, token()).map{ it.body() }
    }

    fun commentsRx(owner: String, repo: String, number: Long): Observable<List<GithubModel.Comment>> {
        return pages(githubApi.commentsRx(owner, repo, number, token())){
            githubApi.commentsRx(owner, repo, number, token(), it)
        }.flatten()
    }

    fun reviewCommentsRx(owner: String, repo: String, number: Long): Observable<List<GithubModel.ReviewComment>> {
        return pages(githubApi.reviewCommentsRx(owner, repo, number, token())){
            githubApi.reviewCommentsRx(owner, repo, number, token(), it)
        }.flatten()
    }

    fun subscriptionsRx(participating: Boolean): Observable<List<GithubModel.Repository>> {
        return pages(githubApi.subscriptionsRx(token(), participating)){
            githubApi.subscriptionsRx(token(), participating, it)
        }.flatten()
    }

    fun notificationsRx(): Observable<List<GithubModel.Notification>> {
        return pages(githubApi.notificationsRx(token())){
            githubApi.notificationsRx(token(), it)
        }.flatten()
    }

    fun pullRequestsRx(owner: String, repo: String): Observable<List<GithubModel.PullRequest>> {
        return pages(githubApi.pullRequestsRx(token(), owner, repo)){
            githubApi.pullRequestsRx(token(), owner, repo, it)
        }.flatten()
    }

    fun token() = "token ${auth.token}"
}