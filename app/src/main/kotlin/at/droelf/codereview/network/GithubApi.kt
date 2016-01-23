package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable

interface GithubApi {

    @GET("/repos/{owner}/{repo}/pulls/{number}/files")
    fun pullRequestFilesRx(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("number") number: Long,
            @Header("Authorization") auth: String,
            @Query("page") page: Int = 1
    ): Observable<Response<MutableList<GithubModel.PullRequestFile>>>

    @GET
    fun fileRx(
            @Url url: String,
            @Header("Accept") accept: String,
            @Header("Authorization") auth: String
    ): Observable<Response<ResponseBody>>

    @GET("/repos/{owner}/{repo}/issues/{number}/comments")
    fun commentsRx(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("number") number: Long,
            @Header("Authorization") auth: String
    ): Observable<Response<MutableList<GithubModel.Comment>>>

    @GET("/repos/{owner}/{repo}/pulls/{number}/comments")
    fun reviewCommentsRx(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("number") number: Long,
            @Header("Authorization") auth: String
    ): Observable<Response<MutableList<GithubModel.ReviewComment>>>

    @GET("/user/subscriptions")
    fun subscriptionsRx(
            @Header("Authorization") auth: String,
            @Query("participating") participating: Boolean
    ): Observable<Response<MutableList<GithubModel.Repository>>>

    @GET("/notifications")
    fun notificationsRx(
            @Header("Authorization") auth: String
    ): Observable<Response<MutableList<GithubModel.Notification>>>

    @GET("/repos/{owner}/{repo}/pulls")
    fun pullRequestsRx(
            @Header("Authorization") auth: String,
            @Path("owner") owner: String,
            @Path("repo") repo: String
    ): Observable<Response<MutableList<GithubModel.PullRequest>>>

}