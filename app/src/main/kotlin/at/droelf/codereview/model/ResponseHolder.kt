package at.droelf.codereview.model

import java.util.concurrent.TimeUnit

class ResponseHolder<E>(
        val data: E,
        val source: Source,
        val timeStamp: Long = System.currentTimeMillis(),
        val alwaysUpToDate: Boolean = false,
        val notUpToDate: Boolean = false) {

    fun upToDate(): Boolean {
        if(alwaysUpToDate) return true
        if(notUpToDate) return false
        val maxAge = TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES)
        return (System.currentTimeMillis() - timeStamp) < maxAge
    }

    enum class Source {
        Network, Memory, Disc
    }
}