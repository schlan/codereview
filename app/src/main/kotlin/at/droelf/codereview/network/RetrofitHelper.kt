package at.droelf.codereview.network

import retrofit2.Response
import rx.Observable
import timber.log.Timber
import timber.log.Timber.*
import java.io.IOException

interface RetrofitHelper {
    fun <E> validateResponse(observable: Observable<Response<E>>): Observable<Response<E>> {
        return observable.flatMap { t ->
            if(t.isSuccessful){
                Observable.just(t)
            } else {
                val e = IOException(t.errorBody().string())
                w("Bad network call :(", e)
                Observable.error(e)
            }
        }
    }
}