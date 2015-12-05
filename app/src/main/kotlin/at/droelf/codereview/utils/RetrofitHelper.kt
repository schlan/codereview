package at.droelf.codereview.utils

import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit


interface RetrofitHelper {
    fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, error: (throwable: Throwable) -> Unit) {
        enqueue(object : Callback<T> {
            override fun onResponse(response: Response<T>, retrofit: Retrofit) {
                success(response)
            }

            override fun onFailure(t: Throwable) {
                error(t)
            }
        })
    }
}