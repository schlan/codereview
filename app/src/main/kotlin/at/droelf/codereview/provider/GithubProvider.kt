package at.droelf.codereview.provider

import android.util.LruCache
import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.storage.PersistantCache
import com.google.gson.reflect.TypeToken
import com.jakewharton.disklrucache.DiskLruCache
import rx.Observable
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit


class GithubProvider(val githubService: GithubService, val cache: LruCache<String, Any>, val diskCache: DiskLruCache) {

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

    fun subscriptions(participating: Boolean, skipCache: Boolean = false): Observable<List<GithubModel.Repository>> {
        val key = "subscriptions-$participating"
        return genericLoadData(key, githubSubscriptions, githubService.subscriptionsRx(participating), skipCache)
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

    private fun <E> genericLoadData(key: String, cache: GithubEndpointCache<E>, nwObservable: Observable<ResponseHolder<E>>, skipCache: Boolean): Observable<E>
            where E : Any {
        val network = nwObservable.doOnNext { cache.put(key, it.data) }

        if(!skipCache){
            val memory = cache.get(key)
            return Observable.concat(memory, network)
                    .first { d -> d.upToDate() }
                    .map { d -> d.data }
        } else {
            return network.map { d -> d.data }
        }
    }

    private fun <E> genericLoadDataV2(key: String, memory: GithubEndpointCache<E>, disc: PersistantCache<E>, network: Observable<ResponseHolder<E>>, clazz: Type, skipCache: Boolean): Observable<ResponseHolder<E>>
            where E : Any {

        val networkObservable = network.doOnNext { memory.put(key, it.data) }.doOnNext { disc.put(key, it.data) }
        val data: Observable<ResponseHolder<E>>

        if(!skipCache){
            val memoryObservable = memory.get(key).cache()
            val discObservable = disc.get(key, clazz).cache().doOnNext { memory.put(key, it.data) }
            data = Observable.concat(
                    memoryObservable.takeFirst { it.upToDate() },
                    memoryObservable.takeFirst { true },
                    discObservable.takeFirst { it.upToDate() },
                    discObservable.takeFirst { true },
                    networkObservable)
                .takeUntil {
                    it.upToDate()
                }
                .distinctUntilChanged {
                    it.upToDate()
                }
                .onErrorResumeNext { error ->
                    error.printStackTrace()
                    println("Error! Trying to load data from network. `${error.message}`")
                    networkObservable
                }
        } else {
            val memoryObservable = memory.get(key).cache()
            val discObservable = disc.get(key, clazz).cache().doOnNext { memory.put(key, it.data) }
            data = Observable.concat(
                    memoryObservable.takeFirst { true }.map { ResponseHolder(it.data, it.source, it.timeStamp, notUpToDate = true) },
                    discObservable.takeFirst { true }.map { ResponseHolder(it.data, it.source, it.timeStamp, notUpToDate = true) },
                    networkObservable)
                    .takeUntil {
                        it.upToDate()
                    }
                    .distinctUntilChanged {
                        it.upToDate()
                    }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        println("Error! Trying to load data from network. `${error.message}`")
                        networkObservable
                    }
        }

        return data
    }

}