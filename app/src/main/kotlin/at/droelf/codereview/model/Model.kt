package at.droelf.codereview.model

import android.text.SpannableString
import at.droelf.codereview.patch.Patch

object Model{
    data class GithubAuth(val token: String)

    data class GithubDataSet(val patch: Patch.Patch, val fileContent: List<SpannableString>, val comments: List<GithubModel.Comment>, val reviewComments: List<GithubModel.ReviewComment>)
}