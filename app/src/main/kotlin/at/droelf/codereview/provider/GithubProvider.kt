package at.droelf.codereview.provider

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.storage.GithubUserStorage
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import rx.Observable
import java.lang.reflect.Type

class GithubProvider(
        val githubService: GithubService,
        val githubUserStorage: GithubUserStorage,
        val githubCache: GithubCacheHolder): GithubCacheHelper {

    fun pullRequests(owner: String, repo: String, skipCache: Boolean = false): Observable<ResponseHolder<List<GithubModel.PullRequest>>> {
        val key = "pull_requests-$owner-$repo"
        val t: Type = object: TypeToken<ResponseHolder<List<GithubModel.PullRequest>>>(){}.type
        return persistentCacheFlow(key, githubCache.githubPrCache, githubCache.githubPrStorage, githubService.pullRequestsRx(owner, repo), t, skipCache)
    }

    fun pullRequestDetail(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<GithubModel.PullRequestDetail> {
        val key = "pull_requests_detail-$owner-$repo-$number"
        return memoryCacheFlow(key, githubCache.githubPrDetailCache, githubService.pullRequestDetailRx(owner, repo, number), skipCache)
    }

    fun pullRequestFiles(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.PullRequestFile>> {
        val key = "pull_request_files-$owner-$repo-$number"
        return memoryCacheFlow(key, githubCache.githubPrFilesCache, githubService.pullRequestFilesRx(owner, repo, number), skipCache)
    }

    fun comments(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.Comment>> {
        val key = "pull_request_comments-$owner-$repo-$number"
        val t: Type = object: TypeToken<ResponseHolder<List<GithubModel.Comment>>>(){}.type
        return persistentCacheFlow(key, githubCache.githubPrCommentsCache, githubCache.githubPrCommentsStorage, githubService.commentsRx(owner, repo, number), t, skipCache)
                .map { it.data }
    }

    fun reviewComments(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<List<GithubModel.ReviewComment>> {
        val key = "pull_request_review_comments-$owner-$repo-$number"
        val t: Type = object: TypeToken<ResponseHolder<List<GithubModel.ReviewComment>>>(){}.type
        return persistentCacheFlow(key, githubCache.githubPrReviewCommentsCache, githubCache.githubPrReviewCommentsStorage, githubService.reviewCommentsRx(owner, repo, number), t, skipCache)
                .map { it.data }
    }

    fun subscriptions(skipCache: Boolean = false): Observable<List<Model.GithubSubscription>> {
        val key = "subscriptions"
        return memoryCacheFlow(key, githubCache.githubSubscriptions, githubService.subscriptionsRx(), skipCache)
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
        return memoryCacheFlow(key, githubCache.githubNotifications, githubService.notificationsRx(), skipCache)
    }

    fun file(url: String, accept: String, skipCache: Boolean = false): Observable<String> {
        val key = "file_$url-$accept"
        return memoryCacheFlow(key, githubCache.githubFile, githubService.fileRx(url, accept), skipCache)
    }

    fun status(owner: String, repo: String, ref: String, skipCache: Boolean = false): Observable<List<GithubModel.Status>> {
        val key = "status_$owner-$repo-$ref"
        return memoryCacheFlow(key, githubCache.githubStatus, githubService.statusRx(owner, repo, ref), skipCache)
    }

    fun emoji(): Observable<ResponseHolder<Map<String, String>>> {
        val key = "emoji"
        val t: Type = object: TypeToken<ResponseHolder<Map<String, String>>>(){}.type
        return persistentCacheFlow(key, githubCache.githubEmojiCache, githubCache.githubEmojiStorage, githubService.emojiRx(), t, false)
    }

    fun reactions(): Observable<List<GithubModel.ReactionItem>> {
        val key = "reaction_items"
        val reactions = Observable.zip(
                emoji(), Observable.just(GithubModel.ReactionType.values()),
                { emojiMap, reactions ->
                    reactions.map { GithubModel.ReactionItem(it, emojiMap.data[it.emojiName].orEmpty()) }
                })
                .map { ResponseHolder(it, ResponseHolder.Source.Network) }

        return memoryCacheFlow(key, githubCache.reactionItemCache, reactions, false)
    }

    fun createReviewComment(owner: String, repo: String, number: Long, createReviewComment: GithubModel.CreateReviewComment): Call<ResponseBody> {
        return githubService.createReviewComment(owner, repo, number, createReviewComment)
    }

    fun createReviewComment(owner: String, repo: String, number: Long, replyReviewComment: GithubModel.ReplyReviewComment): Call<ResponseBody> {
        return githubService.createReviewComment(owner, repo, number, replyReviewComment)
    }

    fun issueReactions(owner: String, repo: String, number: Long, skipCache: Boolean = false): Observable<Map<GithubModel.ReactionItem, Int>> {
        val key = "issue_reaction_$owner-$repo-$number"
        val issueReactions = extractReactions(githubService.issueReactions(owner, repo, number))
        return memoryCacheFlow(key, githubCache.issueReactionCache, issueReactions, skipCache)
    }

    private fun extractReactions(rawReactions: Observable<ResponseHolder<List<GithubModel.RawReaction>>>): Observable<ResponseHolder<Map<GithubModel.ReactionItem, Int>>> {
        return Observable.zip(reactions(), rawReactions,
                { baseReactions, rawReactionsResult ->
                    val data = baseReactions.mapNotNull { reaction ->
                        val count = rawReactionsResult.data.count { it.content == reaction.type.content }
                        if(count > 0){
                            (reaction to count)
                        } else {
                            null
                        }
                    }.toMap()
                    ResponseHolder(data, rawReactionsResult.source, rawReactionsResult.timeStamp, rawReactionsResult.alwaysUpToDate, rawReactionsResult.notUpToDate)
                }
        )
    }
}