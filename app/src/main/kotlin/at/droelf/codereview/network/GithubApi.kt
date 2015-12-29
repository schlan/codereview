package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import com.squareup.okhttp.ResponseBody
import retrofit.http.*
import rx.Observable

interface GithubApi {

    @GET("/repos/{owner}/{repo}/pulls/{number}/files")
    fun pullRequestFilesRx(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("number") number: Long,
            @Header("Authorization") auth: String
    ): Observable<MutableList<GithubModel.PullRequestFile>>

    @GET
    fun fileRx(
            @Url url: String,
            @Header("Accept") accept: String,
            @Header("Authorization") auth: String
    ): Observable<ResponseBody>

    @GET("/repos/{owner}/{repo}/issues/{number}/comments")
    fun commentsRx(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Header("Authorization") auth: String
    ): Observable<MutableList<GithubModel.Comment>>

    @GET("/repos/{owner}/{repo}/pulls/{number}/comments")
    fun reviewCommentsRx(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("number") number: Int,
            @Header("Authorization") auth: String
    ): Observable<MutableList<GithubModel.ReviewComment>>

    @GET("/user/subscriptions")
    fun subscriptionsRx(
            @Header("Authorization") auth: String,
            @Query("participating") participating: Boolean
    ): Observable<MutableList<GithubModel.Repository>>


    @GET("/notifications")
    fun notificationsRx(
            @Header("Authorization") auth: String
    ): Observable<MutableList<GithubModel.Notification>>
}