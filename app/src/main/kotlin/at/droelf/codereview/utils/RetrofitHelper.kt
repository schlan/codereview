package at.droelf.codereview.utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


interface RetrofitHelper {
    fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, error: (throwable: Throwable) -> Unit) {
        enqueue(object : Callback<T> {
            override fun onFailure(t: Throwable) {
                error(t)
            }

            override fun onResponse(response: Response<T>) {
                success(response)
            }
        })
    }
}