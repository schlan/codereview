package at.droelf.codereview.cache

import android.util.LruCache
import at.droelf.codereview.model.ResponseHolder
import rx.Observable

class GithubEndpointCache<E>(val cache: LruCache<String, Any>) where E : Any {

    fun get(key: String): Observable<ResponseHolder<E>> {
        return Observable.create {
            val data = cache.get(key)
            if(data != null){
                @Suppress("UNCHECKED_CAST")
                it.onNext(data as ResponseHolder<E>)
            }
            it.onCompleted()
        }
    }

    fun put(key: String, data: E){
        cache.put(key, ResponseHolder(data, ResponseHolder.Source.Memory))
    }
}