package at.droelf.codereview.patch

import org.junit.Test
import rx.Observable
import rx.Subscriber
import rx.Subscription
import java.util.*
import java.util.concurrent.TimeUnit

class RxTests {

    @Test
    fun testObservableLists() {
        val list: List<Observable<String>> = (0..100).map { Observable.just(it.toString()) }
        val mergedObservables: Observable<String> = Observable.merge(list)
        val mergedObservablesList: Observable<List<String>> = mergedObservables.toList()

        println("Observables: ${mergedObservables.toBlocking().toIterable().toList()}")
    }


    @Test
    fun testRxCaching() {
        val memoryCache: MutableMap<String, Data<String>> = hashMapOf()
        val discCache: MutableMap<String, Data<String>> = hashMapOf()

        fun crash(p: Int): Boolean {
            return Random().nextInt(100) < p
        }

        fun network(key: String): Observable<Data<String>> {
            return Observable.just(key)
                    .delay(2, TimeUnit.SECONDS)
                    .map{ Data(it, Source.Network, System.currentTimeMillis()) }
                    .doOnNext { memoryCache.put(key, Data(it.data, Source.Memory, it.timestamp)) }
                    .doOnNext { discCache.put(key, Data(it.data, Source.Disc, it.timestamp)) }
                    .doOnNext { println("  Network: $it") }
        }


        fun memory(key: String): Observable<Data<String>> {
            return Observable.create({
                if(crash(5)){
                    it.onError(IllegalArgumentException("memory error"))
                } else {
                    if (memoryCache.containsKey(key)) {
                        it.onNext(memoryCache[key])
                        println("  Memory: ${memoryCache[key]}")
                    } else {
                        println("  Memory: Nope")
                    }

                    it.onCompleted()
                }
            })
        }

        fun disc(key: String): Observable<Data<String>> {
            return Observable.create(Observable.OnSubscribe<Data<String>> {
                    if (crash(25)) {
                        it.onError(IllegalArgumentException("disc error"))
                    } else {
                        if (discCache.containsKey(key)) {
                            it.onNext(discCache[key])
                            println("  Disc: ${discCache[key]}")
                        } else {
                            println("  Disc: Nope")
                        }
                        it.onCompleted()
                    }
                })
                    .doOnNext { memoryCache.put(key, Data(it.data, Source.Memory, it.timestamp)) }
                    .delay(1, TimeUnit.SECONDS)
        }

        fun observable(key: String): Observable<Data<String>> {
            val mem = memory(key).cache()
            val disc =  disc(key).cache()

            return Observable.concat(
                        mem.takeFirst { it.upToDate() },
                        mem.takeFirst { true },
                        disc.takeFirst { it.upToDate() },
                        disc.takeFirst { true },
                        network(key))
                    .takeUntil { it.upToDate() }
                    .distinctUntilChanged { it.upToDate() }
                    .onErrorResumeNext { error ->
                        println("Error! Trying to load data from network. `${error.message}`")
                        network(key)
                    }
        }

        println("------")
        println("\nResult: ${observable("fooBar").toBlocking().toIterable().toList()}\n")

        Thread.sleep(4000)

        println("------")
        println("\nResult: ${observable("fooBar").toBlocking().toIterable().toList()}\n")

        memoryCache.clear()

        Thread.sleep(1000)

        println("------")
        println("\nResult: ${observable("fooBar").toBlocking().toIterable().toList()}\n")

        Thread.sleep(1000)

        println("------")
        println("\nResult: ${observable("fooBar2").toBlocking().toIterable().toList()}\n")

        Thread.sleep(1000)

        println("------")
        println("\nResult: ${observable("fooBar2").toBlocking().toIterable().toList()}\n")

        Thread.sleep(7000)
        memoryCache.clear()

        println("------")
        println("\nResult: ${observable("fooBar2").toBlocking().toIterable().toList()}\n")
    }

    open class Data<T>(val data: T, val source: Source, val timestamp: Long) {
        open fun upToDate(): Boolean {
            val upToDate = (System.currentTimeMillis() - timestamp) < TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)
            //println("    UpToDate: $source: $upToDate")
            return upToDate
        }

        override fun toString(): String {
            return "Data(data=$data, source=$source, timestamp=$timestamp, upToDate=${upToDate()})"
        }

    }

    enum class Source {
        Network, Memory, Disc
    }

}