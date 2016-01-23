package at.droelf.codereview.network

import okhttp3.Headers
import okhttp3.HttpUrl
import retrofit2.Response
import rx.Observable

interface GithubPagination {

    fun <T> Observable<List<MutableList<T>>>.flatten(): Observable<List<T>> {
        return map { it.flatten() }
    }

    fun <T> pages(initialResponse: Observable<Response<T>>, loadNextPage: (page: Int) -> Observable<Response<T>>): Observable<List<T>> {
        return initialResponse.flatMap { data ->
            val lastPage = parseHeader(data.headers())

            if(lastPage == null) {
                Observable.just(data)
            } else {
                val pages = listOf<Observable<Response<T>>>() + Observable.just(data) + (2..lastPage).map(loadNextPage)
                Observable.merge(pages)
            }
        }.map{ it.body() }.toList()
    }


    fun parseHeader(headers: Headers): Int? {
        val pages = headers.get("Link") ?: return null
        val lastPage = pages.split(',').filter{ it.contains("last") }.firstOrNull() ?: return null
        val start = lastPage.indexOf("<") + 1
        val end = lastPage.indexOf(">")
        val page: String? = HttpUrl.parse(lastPage.substring(start, end)).queryParameter("page")

        try {
            return page?.toInt()
        }catch(e: NumberFormatException){
            return null
        }
    }
}