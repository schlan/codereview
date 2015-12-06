package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.ResponseBody
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import retrofit.Call
import retrofit.Retrofit
import retrofit.GsonConverterFactory
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.Path
import retrofit.http.Url
import rx.Observable

class GithubService(val auth: Model.GithubAuth, val githubApi: GithubApi) {

    fun pullRequestFilesRx(owner: String, repo: String, number: Int): Observable<MutableList<GithubModel.PullRequestFile>> {
        return githubApi.pullRequestFilesRx(owner, repo, number, token())
    }

    fun fileRx(url: String, contentType: String): Observable<ResponseBody> {
        return githubApi.fileRx(url, contentType, token())
    }

    fun commentsRx(owner: String, repo: String, number: Int): Observable<MutableList<GithubModel.Comment>> {
        return githubApi.commentsRx(owner, repo, number, token())
    }

    fun reviewCommentsRx(owner: String, repo: String, number: Int): Observable<MutableList<GithubModel.ReviewComment>> {
        return githubApi.reviewCommentsRx(owner, repo, number, token())
    }

    fun subscriptionsRx(): Observable<MutableList<GithubModel.Subscription>> {
        return githubApi.subscriptionsRx(token())
    }

    fun token() = "token ${auth.token}"
}