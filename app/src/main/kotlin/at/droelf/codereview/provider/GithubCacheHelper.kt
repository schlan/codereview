package at.droelf.codereview.provider

import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.storage.PersistantCache
import rx.Observable
import java.lang.reflect.Type

interface GithubCacheHelper {
    fun <E> genericLoadData(key: String, cache: GithubEndpointCache<E>, nwObservable: Observable<ResponseHolder<E>>, skipCache: Boolean): Observable<E>
            where E : Any {
        val network = nwObservable.doOnNext { cache.put(key, it.data) }

        if(!skipCache){
            val memory = cache.get(key)
            return Observable.concat(memory, network)
                    .first { d -> d.upToDate() }
                    .map { d -> d.data }
        } else {
            return network.map { d -> d.data }
        }
    }

    fun <E> genericLoadDataV2(key: String, memory: GithubEndpointCache<E>, disc: PersistantCache<E>, network: Observable<ResponseHolder<E>>, clazz: Type, skipCache: Boolean): Observable<ResponseHolder<E>>
            where E : Any {

        val networkObservable = network.doOnNext { memory.put(key, it.data) }.doOnNext { disc.put(key, it.data) }
        val data: Observable<ResponseHolder<E>>

        if(!skipCache){
            val memoryObservable = memory.get(key).cache()
            val discObservable = disc.get(key, clazz).cache().doOnNext { memory.put(key, it.data) }
            data = Observable.concat(
                    memoryObservable.takeFirst { it.upToDate() },
                    memoryObservable.takeFirst { true },
                    discObservable.takeFirst { it.upToDate() },
                    discObservable.takeFirst { true },
                    networkObservable)
                    .takeUntil {
                        it.upToDate()
                    }
                    .distinctUntilChanged {
                        it.upToDate()
                    }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        println("Error! Trying to load data from network. `${error.message}`")
                        networkObservable
                    }
        } else {
            val memoryObservable = memory.get(key).cache()
            val discObservable = disc.get(key, clazz).cache().doOnNext { memory.put(key, it.data) }
            data = Observable.concat(
                    memoryObservable.takeFirst { true }.map { ResponseHolder(it.data, it.source, it.timeStamp, notUpToDate = true) },
                    discObservable.takeFirst { true }.map { ResponseHolder(it.data, it.source, it.timeStamp, notUpToDate = true) },
                    networkObservable)
                    .takeUntil {
                        it.upToDate()
                    }
                    .distinctUntilChanged {
                        it.upToDate()
                    }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        println("Error! Trying to load data from network. `${error.message}`")
                        networkObservable
                    }
        }

        return data
    }
}