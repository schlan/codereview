package at.droelf.codereview.provider

import android.util.LruCache
import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.storage.PersistentCache
import com.google.gson.reflect.TypeToken
import com.jakewharton.disklrucache.DiskLruCache
import rx.Observable
import java.lang.reflect.Type


class GithubProvider(
        val githubService: GithubService,
        val githubUserStorage: GithubUserStorage,
        val cache: LruCache<String, Any>,
        val diskCache: DiskLruCache): GithubCacheHelper {

    private val githubPrCache = GithubEndpointCache<List<GithubModel.PullRequest>>(cache)
    private val githubPrStorage = PersistentCache<List<GithubModel.PullRequest>>(diskCache)

    private val githubPrDetailCache = GithubEndpointCache<GithubModel.PullRequestDetail>(cache)
    private val githubPrFilesCache = GithubEndpointCache<List<GithubModel.PullRequestFile>>(cache)

    private val githubPrCommentsCache = GithubEndpointCache<List<GithubModel.Comment>>(cache)
    private val githubPrCommentsStorage = PersistentCache<List<GithubModel.Comment>>(diskCache)

    private val githubPrReviewCommentsCache = GithubEndpointCache<List<GithubModel.ReviewComment>>(cache)
    private val githubPrReviewCommentsStorage = PersistentCache<List<GithubModel.ReviewComment>>(diskCache)

    private val githubSubscriptions = GithubEndpointCache<List<GithubModel.Repository>>(cache)
    private val githubNotifications = GithubEndpointCache<List<GithubModel.Notification>>(cache)
    private val githubFile = GithubEndpointCache<String>(cache)
    private val githubStatus = GithubEndpointCache<List<GithubModel.Status>>(cache)

    fun pullRequests(owner: String, repo: String, skipCache: Boolean = false): Observable<ResponseHolder<List<GithubModel.PullRequest>>> {
        val key = "pull_requests-$owner-$repo"
        val t: Type = object: TypeToken<ResponseHolder<List<GithubModel.PullRequest>>>(){}.type
        return genericLoadDataV2(key, githubPrCache, githubPrStorage, githubService.pullRequestsRx(owner, repo), t, skipCache)
    }

    fun pullRequestDetail(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<GithubModel.PullRequestDetail> {
        val key = "pull_requests_detail-$owner-$repo-$number"
        return genericLoadData(key, githubPrDetailCache, githubService.pullRequestDetailRx(owner, repo, number), skipCache)
    }

    fun pullRequestFiles(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.PullRequestFile>> {
        val key = "pull_request_files-$owner-$repo-$number"
        return genericLoadData(key, githubPrFilesCache, githubService.pullRequestFilesRx(owner, repo, number), skipCache)
    }

    fun comments(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.Comment>> {
        val key = "pull_request_comments-$owner-$repo-$number"
        val t: Type = object: TypeToken<ResponseHolder<List<GithubModel.Comment>>>(){}.type
        return genericLoadDataV2(key, githubPrCommentsCache, githubPrCommentsStorage, githubService.commentsRx(owner, repo, number), t, skipCache)
                .map { it.data }
    }

    fun reviewComments(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.ReviewComment>> {
        val key = "pull_request_review_comments-$owner-$repo-$number"
        val t: Type = object: TypeToken<ResponseHolder<List<GithubModel.ReviewComment>>>(){}.type
        return genericLoadDataV2(key, githubPrReviewCommentsCache, githubPrReviewCommentsStorage, githubService.reviewCommentsRx(owner, repo, number), t, skipCache)
                .map { it.data }
    }

    fun subscriptions(skipCache: Boolean = false): Observable<List<Model.GithubSubscription>> {
        val key = "subscriptions"
        return genericLoadData(key, githubSubscriptions, githubService.subscriptionsRx(), skipCache)
                .map { repos ->
                    val configs = githubUserStorage.getRepoConfigurations()
                    repos.map { repo ->
                        val c = configs.find { it.id == repo.id } ?: Model.RepoConfiguration(repo.id, Model.WatchType.All, Model.WatchType.All)
                        Model.GithubSubscription(repo, c)
                    }
                }
                .doOnNext { repos -> githubUserStorage.storeRepoConfiguration( repos.map{ it.config })}
    }

    fun notifications(skipCache: Boolean = false): Observable<List<GithubModel.Notification>> {
        val key = "notifications"
        return genericLoadData(key, githubNotifications, githubService.notificationsRx(), skipCache)
    }

    fun file(url: String, accept: String, skipCache: Boolean = false): Observable<String> {
        val key = "file_$url-$accept"
        return genericLoadData(key, githubFile, githubService.fileRx(url, accept), skipCache)
    }

    fun status(owner: String, repo: String, ref: String, skipCache: Boolean = false): Observable<List<GithubModel.Status>> {
        val key = "status_$owner-$repo-$ref"
        return genericLoadData(key, githubStatus, githubService.statusRx(owner, repo, ref), skipCache)
    }
}