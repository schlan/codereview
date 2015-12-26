package at.droelf.codereview.model

import android.text.SpannableString
import at.droelf.codereview.patch.Patch

object Model {
    data class GithubAuth(val token: String)

    data class GithubDataSet(
            val patch: Patch.Patch,
            val fileContent: List<SpannableString>,
            val comments: List<GithubModel.Comment>,
            val reviewComments: List<Model.ReviewComment>,
            val fileName: String
    )

    data class ReviewComment(
            val id: Long, val body: String, val user: GithubModel.User, val position: Int, val originalPosition: Int, val diffHunk: Patch.Patch,
            val path: String
    )
}