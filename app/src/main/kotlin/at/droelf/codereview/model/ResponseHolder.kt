package at.droelf.codereview.model

import java.util.concurrent.TimeUnit

class ResponseHolder<E>(
        val data: E,
        val source: Source,
        val timeStamp: Long = System.currentTimeMillis(),
        private val upToDate: (timeStamp: Long) -> Boolean = {
            val maxAge = TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES)
            (System.currentTimeMillis() - timeStamp) < maxAge
        }) {

    fun upToDate(): Boolean {
        return upToDate(timeStamp)
    }

    enum class Source {
        Network, Memory
    }
}