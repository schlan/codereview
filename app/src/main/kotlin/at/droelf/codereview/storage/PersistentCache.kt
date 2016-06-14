package at.droelf.codereview.storage

import at.droelf.codereview.model.ResponseHolder
import com.google.gson.Gson
import com.jakewharton.disklrucache.DiskLruCache
import rx.Observable
import timber.log.Timber.d
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

class PersistentCache<E>(val diskCache: DiskLruCache, val infiniteCache: Boolean = false, val gson: Gson = Gson()) where E : Any {

    private val md = MessageDigest.getInstance("MD5")
    private val charset = Charset.forName("UTF-8")

    fun get(key: String, clazz: Type): Observable<ResponseHolder<E>> {
        return Observable.defer {
            val data = synchronized(diskCache){
                diskCache.get(normalizeKey(key))
            }

            if (data != null && data.getLength(0) > 0) {
                val json = data.getString(0)
                d("Load from disc: $clazz ${Thread.currentThread().name}")

                val responseholder = gson.fromJson<ResponseHolder<E>>(json, clazz)
                if(!infiniteCache){
                    Observable.just(responseholder)
                } else {
                    Observable.just(ResponseHolder(responseholder.data, responseholder.source, responseholder.timeStamp, true, responseholder.notUpToDate))
                }
            } else {
                Observable.empty()
            }
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