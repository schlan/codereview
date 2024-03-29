package at.droelf.codereview

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import prettify.PrettifyParser
import rx.Observable
import syntaxhighlight.Parser
import timber.log.Timber
import kotlin.text.Regex

object PrettyfyHighlighter {

    private val colors: Map<String, String> = hashMapOf(
//        "typ" to "87cefa",
//        "kwd" to "00ff00",
//        "lit" to "ffff00",
//        "com" to "999999",
//        "str" to "ff4500",
//        "pun" to "eeeeee",
//        "pln" to "ffffff"
            "typ" to "333333",
            "kwd" to "a71d5d",
            "lit" to "a71d5d",
            "com" to "969896",
            "str" to "183691",
            "pun" to "333333",
            "pln" to "333333",
            "tag" to "63a35c",
            "atn" to "795da3",
            "atv" to "183691"
    )

    private val parser: Parser = PrettifyParser()

    fun highlight(sourceCode: String, fileExtension: String?): Observable<List<SpannableString>> {
        return Observable.defer {
            val prettyCode = prettyfyCode(sourceCode, fileExtension)
            val newlinesIndex = prettyCode.mapIndexed { i, c ->  if(c.equals('\n')) i else Int.MIN_VALUE }.filter { it != Int.MIN_VALUE }

            var ranges: List<Pair<Int,Int>> = newlinesIndex.subList(0, newlinesIndex.lastIndex).zip(newlinesIndex.subList(1, newlinesIndex.lastIndex + 1))
            ranges = listOf(Pair(0, newlinesIndex.first())) + ranges

            if(newlinesIndex.last() != prettyCode.lastIndex) {
                ranges += listOf(Pair(newlinesIndex.last(), prettyCode.lastIndex))
            }

            val data = ranges.map { i ->
                val start = if(prettyCode[i.first] == '\n') i.first + 1 else i.first
                val end: Int = if(prettyCode[i.second] ==  '\n') i.second else i.second + 1

                val string: CharSequence = if(start > end){
                    ""
                } else {
                    prettyCode.subSequence(start, end)
                }

                SpannableString(string)
            }

            Observable.just(data)
        }
    }

    private fun prettyfyCode(sourceCode: String, fileExtension: String?): SpannableString {
        val prettyCode = SpannableString(sourceCode)
        val parserResult = parser.parse(fileExtension, sourceCode)

        parserResult.forEach { r ->
            prettyCode.setSpan(ForegroundColorSpan(colorForKeyword(r.styleKeys.first())), r.offset, r.offset + r.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return prettyCode
    }

    private fun colorForKeyword(keyWord: String): Int {
        return when{
            colors.containsKey(keyWord) -> Color.parseColor("#${colors[keyWord]}")
            else -> {
                Timber.d("Keyword not found: $keyWord")
                Color.parseColor("#${colors["pln"]}")
            }
        }
    }

}