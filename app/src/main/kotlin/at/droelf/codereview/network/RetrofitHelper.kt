package at.droelf.codereview.network

import retrofit2.Response
import rx.Observable
import java.io.IOException

interface RetrofitHelper {
    fun <E> validateResponse(observable: Observable<Response<E>>): Observable<Response<E>> {
        return observable.flatMap { t ->
            if(t.isSuccessful){
                Observable.just(t)
            } else {
                Observable.error(IOException(t.errorBody().string()))
            }
        }
    }
}