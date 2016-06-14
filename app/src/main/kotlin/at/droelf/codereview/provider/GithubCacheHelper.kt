package at.droelf.codereview.provider

import at.droelf.codereview.cache.GithubEndpointCache
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.storage.PersistentCache
import rx.Observable
import rx.schedulers.Schedulers
import timber.log.Timber
import timber.log.Timber.*
import java.lang.reflect.Type

interface GithubCacheHelper {

    fun <E> memoryCacheFlow(key: String, cache: GithubEndpointCache<E>, nwObservable: Observable<ResponseHolder<E>>, skipCache: Boolean): Observable<E>
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

    fun <E> persistentCacheFlow(
            key: String,
            memory: GithubEndpointCache<E>,
            disc: PersistentCache<E>,
            network: Observable<ResponseHolder<E>>,
            clazz: Type,
            skipCache: Boolean
    ): Observable<ResponseHolder<E>> where E : Any {

        val networkObservable = network
                .observeOn(Schedulers.io())
                .doOnNext { memory.put(key, it.data) }
                .doOnNext { disc.put(key, it.data) }

        val memoryObservable = memory.get(key)
                .observeOn(Schedulers.io())
                .cache()

        val discObservable = disc.get(key, clazz)
                .observeOn(Schedulers.io())
                .doOnNext { memory.put(key, it.data) }
                .cache()

        val data: Observable<ResponseHolder<E>>

        if(!skipCache){
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
                        w("Error! Trying to load data from network. `${error.message}`")
                        networkObservable
                    }
        } else {
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
                        w("Error! Trying to load data from network. `${error.message}`")
                        networkObservable
                    }
        }

        return data
    }
}