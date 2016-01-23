package at.droelf.codereview.model

import java.util.*


object GithubModel {

    data class PullRequest(
            val id: Long, val number: Long, val state: String, val title: String, val body: String, val user: User,
            val head: Head, val base: Base, val updatedAt: Date
    )

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

    data class Repository(
            val id: Long, val name: String, val fullName: String, val owner: User, val private: Boolean, val htmlUrl: String, val description: String,
            val fork: Boolean, val createdAt: Date, val updatedAt: Date, val pushedAt: Date, val gitUrl: String, val sshUrl: String, val cloneUrl: String,
            val svnUrl: String, val homepage: String, val size: Long, val stargazersCount: Int, val watchersCount: Int, val language: String, val hasIssues: Boolean,
            val hasDownloads: Boolean, val hasWiki: Boolean, val forksCount: Int, val mirrorUrl: String, val openIssueCount: Int, val forks: Int, val watchers: Int,
            val defaultBranch: String, val permissions: Permissions
    )

    data class Permissions(
            val admin: Boolean, val push: Boolean, val pull: Boolean
    )

    data class Subject(
            val title: String, val url: String, val latestCommitUrl: String, val type: String
    )

    data class Notification (
            val id: Long, val unread: Boolean, val reason: String, val updatedAt: Date, val lastReadAt: Date, val subject: Subject,
            val repository: Repository, val url: String
    )

    data class Head(
            val repo: Repository
    )

    data class Base(
            val repo: Repository
    )
}
