package at.droelf.codereview.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.ResponseBody
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import retrofit.Converter
import retrofit.Retrofit
import retrofit.GsonConverterFactory
import java.lang.reflect.Type

object GithubService {

    private val baseUrl: String = "https://api.github.com"
    private val token: String = "e7cf96ea81ebca1445411b49ebea514f25592641"

    private val gson: Gson
    private val okHttp: OkHttpClient
    private val retrofit: Retrofit

    init {
        gson = GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        val httpLogging = HttpLoggingInterceptor()
        httpLogging.setLevel(HttpLoggingInterceptor.Level.BODY)

        okHttp = OkHttpClient()
        okHttp.interceptors().add(Interceptor { chain ->

            val builder = chain.request().newBuilder()
            builder.addHeader("Authorization", "token $token")
            builder.addHeader("User-Agent", "CodeReview @dr03lf")
            builder.addHeader("Content-Type", "application/json; charset=utf-8")
            builder.addHeader("Accept", "application/vnd.github.VERSION.raw+json")

            chain.proceed(builder.build())
        })
        okHttp.interceptors().add(httpLogging)

        retrofit = Retrofit.Builder()
                .client(okHttp)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build()
    }



    fun githubClient(): GithubApi {
        return retrofit.create(GithubApi::class.java)
    }
}