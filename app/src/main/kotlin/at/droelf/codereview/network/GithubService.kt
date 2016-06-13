package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.ResponseHolder
import okhttp3.ResponseBody
import retrofit2.Call
import rx.Observable

class GithubService(val auth: Model.GithubAuth, val githubApi: GithubApi): GithubPagination, RetrofitHelper {

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

    fun subscriptionsRx(): Observable<ResponseHolder<List<GithubModel.Repository>>> {
        return wrap(pages(githubApi.subscriptionsRx(token())){
            githubApi.subscriptionsRx(token(), it)
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
        return wrap(validateResponse(githubApi.pullRequestDetailRx(token(), owner, repo, number)).map { it.body() })
    }

    fun statusRx(owner: String, repo: String, ref: String): Observable<ResponseHolder<List<GithubModel.Status>>> {
        return wrap(pages(githubApi.statusesRx(token(), owner, repo, ref)){
            githubApi.statusesRx(token(), owner, repo, ref, it)
        }.flatten())
    }

    fun emojiRx(): Observable<ResponseHolder<Map<String, String>>>{
        val foo: Observable<Map<String, String>> = pages(githubApi.emojisRx(token())){ githubApi.emojisRx(token(), it) }
                .map{ map -> map.fold(mutableMapOf<String, String>()){ foo, bar -> foo.putAll(bar); foo } }
        return wrap(foo)
    }

    fun createReviewComment(owner: String, repo: String, number: Long, createReviewComment: GithubModel.CreateReviewComment): Call<ResponseBody> {
        return githubApi.createReviewComment(token(), owner, repo, number, createReviewComment)
    }

    fun createReviewComment(owner: String, repo: String, number: Long, replyReviewComment: GithubModel.ReplyReviewComment): Call<ResponseBody>{
        return githubApi.replyReviewComment(token(), owner, repo, number, replyReviewComment)
    }

    fun token() = "token ${auth.auth.token}"

    fun <E> wrap(data: Observable<E>): Observable<ResponseHolder<E>> {
        return data.map { ResponseHolder(it, ResponseHolder.Source.Network, alwaysUpToDate = true) }
    }
}