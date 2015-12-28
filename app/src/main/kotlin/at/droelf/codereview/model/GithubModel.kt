package at.droelf.codereview.model


object GithubModel {

    data class PullRequest(val id: Long, val number: Long, val state: String, val title: String, val body: String)

    data class PullRequestFile(
            val sha: String, val filename: String, val status: String, val additions: Int, val deletions: Int, val changes: Int,
            val patch: String, val blobUrl: String, val rawUrl: String, val contentsUrl: String
    )

    data class Comment(
            val id: Long, val url: String, val htmlUrl: String, val body: String, val user: User
    )

    data class ReviewComment(
            val id: Long, val body: String, val user: User, val position: Long?, val originalPosition: Long, val diffHunk: String,
            val path: String, val bodyHtml: String, val bodyText: String
    )

    data class User(
            val login: String, val id: Long, val avatarUrl: String, val gravatarId: String, val url: String,
            val htmlUrl: String, val followersUrl: String, val followingUrl: String, val type: String, val siteAdmin: Boolean
    )


    data class Subscription(
            val id: Long, val owner: User, val name: String, val fullName: String, val description: String, val private: Boolean,
            val fork: Boolean, val language: String, val forkCounts: Int, val stargazersCount: Int, val watchersCount: Int,
            val size: Int, val defaultBranch: String, val openIssueCount: Int, val hasIssues: Boolean
    )

}
