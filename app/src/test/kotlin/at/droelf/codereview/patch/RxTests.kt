package at.droelf.codereview.patch

import org.junit.Test
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RxTests {

    @Test
    fun testObservableLists() {
        val list: List<Observable<String>> = (0..100).map { Observable.just(it.toString()) }
        val mergedObservables: Observable<String> = Observable.merge(list)
        val mergedObservablesList: Observable<List<String>> = mergedObservables.toList()

        //println("Observables: ${mergedObservables.toBlocking().toIterable().toList()}")
    }


    @Test
    fun testRxCaching() {
        val memoryCache: MutableMap<String, Data<String>> = hashMapOf()
        val discCache: MutableMap<String, Data<String>> = hashMapOf()

        fun crash(p: Int): Boolean {
            return false //Random().nextInt(100) < p
        }

        fun saveToDisc(key: String, data: Data<String>){
            println("$key: save to disc $data")
            discCache.put(key, data)
        }

        fun saveToMemory(key: String, data: Data<String>){
            println("$key: save to memory $data")
            memoryCache.put(key, data)
        }

        fun network(key: String): Observable<Data<String>> {
            return Observable.just(key)
                    .delay(10, TimeUnit.SECONDS)
                    .map{ Data(it, Source.Network, System.currentTimeMillis()) }
                    .doOnNext { println("$key: Network: $it") }
                    .doOnNext { saveToMemory(key, Data(it.data, Source.Memory, it.timestamp)) }
                    .doOnNext { saveToDisc(key, Data(it.data, Source.Disc, it.timestamp)) }
        }


        fun memory(key: String): Observable<Data<String>> {
            return Observable.create({
                if(crash(5)){
                    it.onError(IllegalArgumentException("memory error"))
                } else {
                    if (memoryCache.containsKey(key)) {
                        it.onNext(memoryCache[key])
                        println("$key: Memory: ${memoryCache[key]}")
                    } else {
                        println("$key: Memory: Nope")
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
                            println("$key: Disc: ${discCache[key]}")
                        } else {
                            println("$key: Disc: Nope")
                        }
                        it.onCompleted()
                    }
                })
                    .delay(3, TimeUnit.SECONDS)
                    .doOnNext { if(it.upToDate()) saveToMemory(key, Data(it.data, Source.Memory, it.timestamp)) }
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
                        println("$key: Error! Trying to load data from network. `${error.message}`")
                        network(key)
                    }
        }

        val latch = CountDownLatch(1)
        val list = Observable.merge((1..10).map{ observable(it.toString()) })
        list.subscribe ({
            println("---------------- $it")
        }, {}, {
            latch.countDown()
        })

        latch.await()

        Thread.sleep(1000)
        println("\n\n\n")
        println(memoryCache.entries.fold("", {a,b -> "$a\n${b.toString()}"}))
        println("\n\n\n")
        println(discCache.entries.fold("", {a,b -> "$a\n${b.toString()}"}))

        println("\n\n\n")

        val latch2 = CountDownLatch(1)

        val list2 = Observable.just("")
            .flatMap {
                Observable.merge((1..10).map{ observable(it.toString()) })
            }
        list2.subscribe({
            println("---------------- $it")
        },{},{
            latch2.countDown()
        })
        latch2.await()
        //println("Foobar: ${list2.toBlocking().toIterable().toList().fold("", { a, b -> "$a\n${b.toString()}"})}")




        //        println("------")
//        println("\nResult: ${observable("fooBar").toBlocking().toIterable().toList()}\n")
//
//        Thread.sleep(4000)
//
//        println("------")
//        println("\nResult: ${observable("fooBar").toBlocking().toIterable().toList()}\n")
//
//        memoryCache.clear()
//
//        Thread.sleep(1000)
//
//        println("------")
//        println("\nResult: ${observable("fooBar").toBlocking().toIterable().toList()}\n")
//
//        Thread.sleep(1000)
//
//        println("------")
//        println("\nResult: ${observable("fooBar2").toBlocking().toIterable().toList()}\n")
//
//        Thread.sleep(1000)
//
//        println("------")
//        println("\nResult: ${observable("fooBar2").toBlocking().toIterable().toList()}\n")
//
//        Thread.sleep(7000)
//        memoryCache.clear()
//
//        println("------")
//        println("\nResult: ${observable("fooBar2").toBlocking().toIterable().toList()}\n")
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