package at.droelf.codereview.provider

import android.util.LruCache
import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.storage.PersistantCache
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
    private val githubPrStorage = PersistantCache<List<GithubModel.PullRequest>>(diskCache)

    private val githubPrDetailCache = GithubEndpointCache<GithubModel.PullRequestDetail>(cache)
    private val githubPrFiles = GithubEndpointCache<List<GithubModel.PullRequestFile>>(cache)
    private val githubPrComments = GithubEndpointCache<List<GithubModel.Comment>>(cache)
    private val githubPrReviewComments = GithubEndpointCache<List<GithubModel.ReviewComment>>(cache)
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
        return genericLoadData(key, githubPrFiles, githubService.pullRequestFilesRx(owner, repo, number), skipCache)
    }

    fun comments(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.Comment>> {
        val key = "pull_request_comments-$owner-$repo-$number"
        return genericLoadData(key, githubPrComments, githubService.commentsRx(owner, repo, number), skipCache)
    }

    fun reviewComments(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.ReviewComment>> {
        val key = "pull_request_review_comments-$owner-$repo-$number"
        return genericLoadData(key, githubPrReviewComments, githubService.reviewCommentsRx(owner, repo, number), skipCache)
    }

    fun subscriptions(participating: Boolean, skipCache: Boolean = false): Observable<List<Model.GithubSubscription>> {
        val key = "subscriptions-$participating"
        return genericLoadData(key, githubSubscriptions, githubService.subscriptionsRx(participating), skipCache)
                .map { repos ->
                    val configs = githubUserStorage.getRepoConfigurations()
                    repos.map { repo ->
                        val c = configs.find { it.id == repo.id } ?: Model.RepoConfiguration(repo.id, Model.WatchType.All, Model.WatchType.All)
                        Model.GithubSubscription(repo, c)
                    }
                }
                .doOnNext { repos ->
                    println("Repos: (${repos.filter { it.config.id == 16692633L }})")
                    githubUserStorage.storeRepoConfiguration( repos.map{ it.config })
                }
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