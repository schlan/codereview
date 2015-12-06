package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import com.squareup.okhttp.ResponseBody
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.Path
import retrofit.http.Url
import rx.Observable

interface GithubApi {

    @GET("/repos/{owner}/{repo}/pulls/{number}/files")
    fun pullRequestFilesRx(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("number") number: Int,
            @Header("Authorization") auth: String
    ): Observable<MutableList<GithubModel.PullRequestFile>>

    @GET
    fun fileRx(
            @Url url: String,
            @Header("Content-Type") contentType: String,
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
            @Header("Authorization") auth: String
    ): Observable<MutableList<GithubModel.Subscription>>


}