package at.droelf.codereview.dagger.services

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import javax.inject.Singleton

@Module
class SquareModule() {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    @Provides
    @Singleton
    fun providesOkhttp(): OkHttpClient {
        val okHttp = OkHttpClient()
        val httpLogging = HttpLoggingInterceptor()
        httpLogging.setLevel(HttpLoggingInterceptor.Level.BODY)

        okHttp.interceptors().add(httpLogging)
        okHttp.interceptors().add(Interceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("User-Agent", "CodeReview @dr03lf")
            builder.addHeader("Content-Type", "application/json; charset=utf-8")
            builder.addHeader("Accept", "application/vnd.github.VERSION.full+json")
            chain.proceed(builder.build())
        })

        return okHttp
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl("https://api.github.com")
            .build()
    }
}