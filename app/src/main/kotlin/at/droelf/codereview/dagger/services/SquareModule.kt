package at.droelf.codereview.dagger.services

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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
        val httpLogging = HttpLoggingInterceptor()
        httpLogging.level = HttpLoggingInterceptor.Level.BODY

        val okHttp = OkHttpClient.Builder()
        okHttp.addInterceptor(httpLogging)
        okHttp.addInterceptor({ chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("User-Agent", "CodeReview @dr03lf")
            builder.addHeader("Content-Type", "application/json; charset=utf-8")
            builder.addHeader("Accept", "application/vnd.github.VERSION.full+json")
            chain.proceed(builder.build())
        })

        return okHttp.build()
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