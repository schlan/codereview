package at.droelf.codereview.provider

import android.util.LruCache
import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.storage.PersistentCache
import com.jakewharton.disklrucache.DiskLruCache

class GithubCacheHolder(val cache: LruCache<String, Any>,
                        val diskCache: DiskLruCache) {

    val githubPrCache = GithubEndpointCache<List<GithubModel.PullRequest>>(cache)
    val githubPrStorage = PersistentCache<List<GithubModel.PullRequest>>(diskCache)

    val githubPrDetailCache = GithubEndpointCache<GithubModel.PullRequestDetail>(cache)
    val githubPrFilesCache = GithubEndpointCache<List<GithubModel.PullRequestFile>>(cache)

    val githubPrCommentsCache = GithubEndpointCache<List<GithubModel.Comment>>(cache)
    val githubPrCommentsStorage = PersistentCache<List<GithubModel.Comment>>(diskCache)

    val githubPrReviewCommentsCache = GithubEndpointCache<List<GithubModel.ReviewComment>>(cache)
    val githubPrReviewCommentsStorage = PersistentCache<List<GithubModel.ReviewComment>>(diskCache)

    val githubEmojiCache = GithubEndpointCache<Map<String, String>>(cache)
    val githubEmojiStorage = PersistentCache<Map<String, String>>(diskCache, infiniteCache = true)

    val githubSubscriptions = GithubEndpointCache<List<GithubModel.Repository>>(cache)
    val githubNotifications = GithubEndpointCache<List<GithubModel.Notification>>(cache)

    val githubFile = GithubEndpointCache<String>(cache)
    val githubStatus = GithubEndpointCache<List<GithubModel.Status>>(cache)

    val reactionItemCache = GithubEndpointCache<List<GithubModel.ReactionItem>>(cache)
    val issueReactionCache = GithubEndpointCache<Map<GithubModel.ReactionItem, Int>>(cache)

}