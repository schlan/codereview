package at.droelf.codereview.model


object GithubModel {

    data class PullRequest(val id: Long, val number: Long, val state: String, val title: String, val body: String)

    data class PullRequestFile(
            val sha: String,
            val filename: String,
            val status: String,
            val additions: Int,
            val deletions: Int,
            val changes: Int,
            val patch: String,
            val blobUrl: String,
            val rawUrl: String,
            val contentsUrl: String
    )
}