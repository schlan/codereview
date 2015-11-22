package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.Path
import java.util.*

interface GithubApi {

    @GET("/repos/{owner}/{repo}/pulls")
    fun pullRequests(
            @Path("owner") owner: String,
            @Path("repo") repo: String
    ): Call<Array<GithubModel.PullRequest>>

    @GET("/repos/{owner}/{repo}/pulls/{number}/files")
    fun pullRequestFiles(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): Call<Array<GithubModel.PullRequestFile>>
}