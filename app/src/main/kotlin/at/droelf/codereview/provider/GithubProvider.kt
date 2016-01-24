package at.droelf.codereview.provider

import android.util.LruCache
import at.droelf.codereview.ResponseHolder
import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import com.couchbase.lite.internal.RevisionInternal
import okhttp3.ResponseBody
import rx.Observable


class GithubProvider(val githubService: GithubService, val cache: LruCache<String, Any>) {

    val githubPrCache = GithubEndpointCache<List<GithubModel.PullRequest>>(cache)
    val githubPrDetailCache = GithubEndpointCache<GithubModel.PullRequestDetail>(cache)
    val githubPrFiles = GithubEndpointCache<List<GithubModel.PullRequestFile>>(cache)
    val githubPrComments = GithubEndpointCache<List<GithubModel.Comment>>(cache)
    val githubPrReviewComments = GithubEndpointCache<List<GithubModel.ReviewComment>>(cache)
    val githubSubscriptions = GithubEndpointCache<List<GithubModel.Repository>>(cache)
    val githubNotifications = GithubEndpointCache<List<GithubModel.Notification>>(cache)
    val githubFile = GithubEndpointCache<String>(cache)

    fun pullRequests(owner: String, repo: String): Observable<List<GithubModel.PullRequest>> {
        val key = "pull_requests-$owner-$repo"
        return genericLoadData(key, githubPrCache, githubService.pullRequestsRx(owner, repo))
    }

    fun pullRequestDetail(owner: String, repo: String, number: Long): Observable<GithubModel.PullRequestDetail> {
        val key = "pull_requests_detail-$owner-$repo-$number"
        return genericLoadData(key, githubPrDetailCache, githubService.pullRequestDetailRx(owner, repo, number))
    }

    fun pullRequestFiles(owner: String, repo: String, number: Long): Observable<List<GithubModel.PullRequestFile>> {
        val key = "pull_request_files-$owner-$repo-$number"
        return genericLoadData(key, githubPrFiles, githubService.pullRequestFilesRx(owner, repo, number))
    }

    fun comments(owner: String, repo: String, number: Long): Observable<List<GithubModel.Comment>> {
        val key = "pull_request_comments-$owner-$repo-$number"
        return genericLoadData(key, githubPrComments, githubService.commentsRx(owner, repo, number))
    }

    fun reviewComments(owner: String, repo: String, number: Long): Observable<List<GithubModel.ReviewComment>> {
        val key = "pull_request_review_comments-$owner-$repo-$number"
        return genericLoadData(key, githubPrReviewComments, githubService.reviewCommentsRx(owner, repo, number))
    }

    fun subscriptions(participating: Boolean): Observable<List<GithubModel.Repository>> {
        val key = "subscriptions-$participating"
        return genericLoadData(key, githubSubscriptions, githubService.subscriptionsRx(participating))
    }

    fun notifications(): Observable<List<GithubModel.Notification>> {
        val key = "notifications"
        return genericLoadData(key, githubNotifications, githubService.notificationsRx())
    }

    fun file(url: String, accept: String): Observable<String> {
        val key = "file_$url-$accept"
        return genericLoadData(key, githubFile, githubService.fileRx(url, accept))
    }

    private fun <E> genericLoadData(key: String, cache: GithubEndpointCache<E>, nwObservable: Observable<ResponseHolder<E>>): Observable<E>
            where E : Any {
        val network = nwObservable.doOnNext { cache.put(key, it.data) }
        val memory = cache.get(key)
        return Observable.concat(memory, network)
                .first { d -> d.upToDate() }
                .map { d -> d.data }
    }

}