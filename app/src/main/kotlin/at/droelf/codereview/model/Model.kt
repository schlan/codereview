package at.droelf.codereview.model

import android.text.SpannableString
import at.droelf.codereview.patch.Patch
import java.util.*

object Model {

    data class GithubAuth(val auth: GithubModel.AuthResponse, val user: GithubModel.User, val email: String, val uuid: UUID)
    data class RepoConfiguration(val id: Long, val pullRequests: WatchType, val issues: WatchType)

    data class GithubSubscription(val repo: GithubModel.Repository, val config: RepoConfiguration)

    data class GithubDataSet(
            val patch: Patch.Patch,
            val fileContent: List<SpannableString>,
            val comments: List<GithubModel.Comment>,
            val reviewComments: List<Model.ReviewComment>,
            val fileName: String
    )

    data class ReviewComment(
            val id: Long, val body: String, val user: GithubModel.User, val position: Int, val originalPosition: Int, val diffHunk: Patch.Patch,
            val path: String, val bodyHtml: String, val bodyText: String, val createdAt: Date?
    )

    enum class WatchType(val id: Int) {
        Hide(0),
        Mine(1),
        All(2),
        Participating(3);

        companion object {
            fun fromId(id: Int): WatchType {
                return WatchType.values().find { it.id == id} ?: All
            }
        }
    }
}