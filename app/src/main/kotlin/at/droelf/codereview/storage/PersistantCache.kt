package at.droelf.codereview.storage

import at.droelf.codereview.model.ResponseHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.disklrucache.DiskLruCache
import rx.Observable
import java.lang.reflect.Type
import java.util.*

class PersistantCache<E>(val diskCache: DiskLruCache) where E : Any {

    fun get(key: String, clazz: Type): Observable<ResponseHolder<E>> {
        return Observable.create {
            val data = diskCache.get(normalizeKey(key))
            if (data != null && data.getLength(0) > 0) {
                val json = data.getString(0)
                println("Load from disc: $clazz")
                val data = Gson().fromJson<ResponseHolder<E>>(json, clazz)
                it.onNext(data)
            }
            it.onCompleted()
        }
    }

    fun put(key: String, data: E){
        val gson = Gson()
        val edit = diskCache.edit(normalizeKey(key))
        edit.set(0, gson.toJson(ResponseHolder(data, ResponseHolder.Source.Disc)))
        edit.commit()
    }

    fun normalizeKey(key: String): String {
        return key.toLowerCase(Locale.ENGLISH)
    }
}