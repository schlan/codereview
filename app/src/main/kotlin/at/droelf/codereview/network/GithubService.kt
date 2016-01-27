package at.droelf.codereview.network

import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import okhttp3.ResponseBody
import rx.Observable

class GithubService(val auth: Model.GithubAuth, val githubApi: GithubApi): GithubPagination {

    fun pullRequestFilesRx(owner: String, repo: String, number: Long): Observable<ResponseHolder<List<GithubModel.PullRequestFile>>> {
        return wrap(pages(githubApi.pullRequestFilesRx(owner, repo, number, token())){
            githubApi.pullRequestFilesRx(owner, repo, number, token(), it)
        }.flatten())
    }

    fun fileRx(url: String, accept: String): Observable<ResponseHolder<String>> {
        return wrap(githubApi.fileRx(url, accept, token()).map{ it.body().string() })
    }

    fun commentsRx(owner: String, repo: String, number: Long): Observable<ResponseHolder<List<GithubModel.Comment>>> {
        return wrap(pages(githubApi.commentsRx(owner, repo, number, token())){
            githubApi.commentsRx(owner, repo, number, token(), it)
        }.flatten())
    }

    fun reviewCommentsRx(owner: String, repo: String, number: Long): Observable<ResponseHolder<List<GithubModel.ReviewComment>>> {
        return wrap(pages(githubApi.reviewCommentsRx(owner, repo, number, token())){
            githubApi.reviewCommentsRx(owner, repo, number, token(), it)
        }.flatten())
    }

    fun subscriptionsRx(participating: Boolean): Observable<ResponseHolder<List<GithubModel.Repository>>> {
        return wrap(pages(githubApi.subscriptionsRx(token(), participating)){
            githubApi.subscriptionsRx(token(), participating, it)
        }.flatten())
    }

    fun notificationsRx(): Observable<ResponseHolder<List<GithubModel.Notification>>> {
        return wrap(pages(githubApi.notificationsRx(token())){
            githubApi.notificationsRx(token(), it)
        }.flatten())
    }

    fun pullRequestsRx(owner: String, repo: String): Observable<ResponseHolder<List<GithubModel.PullRequest>>> {
        return wrap(pages(githubApi.pullRequestsRx(token(), owner, repo)){
            githubApi.pullRequestsRx(token(), owner, repo, it)
        }.flatten())
    }

    fun pullRequestDetailRx(owner: String, repo: String, number: Long): Observable<ResponseHolder<GithubModel.PullRequestDetail>> {
        return wrap(githubApi.pullRequestDetailRx(token(), owner, repo, number).map { it.body() })
    }

    fun token() = "token ${auth.token}"

    fun <E> wrap(data: Observable<E>): Observable<ResponseHolder<E>> {
        return data.map { ResponseHolder(it, ResponseHolder.Source.Network, upToDate = { true }) }
    }
}