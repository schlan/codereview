package at.droelf.codereview.storage

import at.droelf.codereview.model.ResponseHolder
import com.google.gson.Gson
import com.jakewharton.disklrucache.DiskLruCache
import rx.Observable
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

class PersistentCache<E>(val diskCache: DiskLruCache, val gson: Gson = Gson()) where E : Any {

    private val md = MessageDigest.getInstance("MD5")
    private val charset = Charset.forName("UTF-8")

    fun get(key: String, clazz: Type): Observable<ResponseHolder<E>> {
        return Observable.create {
            val data = diskCache.get(normalizeKey(key))
            if (data != null && data.getLength(0) > 0) {
                val json = data.getString(0)
                println("Load from disc: $clazz ${Thread.currentThread().name}")
                it.onNext(gson.fromJson<ResponseHolder<E>>(json, clazz))
            }
            it.onCompleted()
        }
    }

    fun put(key: String, data: E){
        synchronized(diskCache) {
            val edit = diskCache.edit(normalizeKey(key))
            edit.set(0, gson.toJson(ResponseHolder(data, ResponseHolder.Source.Disc)))
            edit.commit()
        }
    }

    private fun byteArray2Hex(hash: ByteArray): String {
        val formatter = Formatter()
        hash.forEach { formatter.format("%02x", it) }
        return formatter.toString()
    }

    private fun normalizeKey(key: String): String {
        return byteArray2Hex(md.digest(key.toByteArray(charset)))
    }
}