package at.droelf.codereview.utils

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


interface RxHelper {
    fun <E>transformObservable(): Observable.Transformer<E, E> = Observable.Transformer {
            it
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
    }
}