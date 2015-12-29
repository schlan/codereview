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

    fun pullRequestFilesRx(owner: String, repo: String, number: Long): Observable<MutableList<GithubModel.PullRequestFile>> {
        return githubApi.pullRequestFilesRx(owner, repo, number, token())
    }

    fun fileRx(url: String, accept: String): Observable<ResponseBody> {
        return githubApi.fileRx(url, accept, token())
    }

    fun commentsRx(owner: String, repo: String, number: Int): Observable<MutableList<GithubModel.Comment>> {
        return githubApi.commentsRx(owner, repo, number, token())
    }

    fun reviewCommentsRx(owner: String, repo: String, number: Int): Observable<MutableList<GithubModel.ReviewComment>> {
        return githubApi.reviewCommentsRx(owner, repo, number, token())
    }

    fun subscriptionsRx(participating: Boolean): Observable<List<GithubModel.Repository>> {
        return githubApi.subscriptionsRx(token(), participating).map { it.toList() }
    }

    fun notificationsRx(): Observable<List<GithubModel.Notification>> {
        return githubApi.notificationsRx(token()).map{ it.toList() }
    }


    fun token() = "token ${auth.token}"
}