package at.droelf.codereview.patch

import android.text.SpannableString
import at.droelf.codereview.PrettyfyHighlighter
import kotlin.text.Regex


object Patch{

    val headerRegex = Regex("^\\s*@@\\ \\-\\d+\\,\\d+\\ \\+\\d+\\,\\d+\\ @@")

    data class Patch(val patchSegments: List<PatchSegment>)
    data class PatchSegment(val originalRange: Range, val newRange: Range, val lines: List<Line>, val header: String, val method: String?)

    data class Line(val line: SpannableString, val type: Type, val originalNum: Int?, val modifiedNum: Int?)
    data class Range(val start: Int, val numLines: Int)

    enum class Type(val id: Int) {
        Add(1), Delete(2), Neutral(3);
    }

    fun parse(data: String): Patch? {
        val lines = PrettyfyHighlighter.highlight(data, "java")
        if(lines.isEmpty()) return null

        val headerIndex = lines.filter { headerRegex.containsMatchIn(it.trim()) }.map { lines.indexOf(it) }
        if(headerIndex.isEmpty()) return null

        val range: List<Pair<Int, Int>> = headerIndex.subList(0, headerIndex.lastIndex).zip(headerIndex.subList(1, headerIndex.lastIndex + 1)) + Pair(headerIndex.last(), lines.lastIndex + 1)
        val patchesRaw: List<List<SpannableString>> = range.map { lines.subList(it.first, it.second) }
        return Patch( patchesRaw.map { parsePatchSegment(it)} )
    }

    private fun parsePatchSegment(list: List<SpannableString>): PatchSegment {
        val header = headerRegex.find(list.first())
        val r: Pair<Range, Range> = parseHeader(header!!.value) //FIXME

        var iOri = r.first.start
        var iMod = r.second.start
        val parsedLines: List<Line> = list.subList(1, list.lastIndex + 1).mapIndexed { i, s ->
            val lineType = parseLineType(s)
            val lineNumber = when(lineType){
                Type.Neutral -> Pair(iOri++, iMod++)
                Type.Add -> Pair(null, iMod++)
                Type.Delete -> Pair(iOri++, null)
            }
            Line(s, lineType, lineNumber.first, lineNumber.second)
        }
        return PatchSegment(r.first, r.second, parsedLines, header.value, list.first().toString().replace(header.value, ""))
    }


    private fun parseHeader(header: String): Pair<Range, Range> {
        val strippedHeader: List<String> = header.replace("@", "").trim().split(" ")
        return Pair(parseRange(strippedHeader.first()), parseRange(strippedHeader.last()))
    }

    private fun parseRange(range: String): Range {
        val r: List<String> = range.substring(1).split(",")
        return Range(r.first().toInt(), r.last().toInt())
    }

    private fun parseLineType(s: SpannableString): Type {
        when {
            s.trim().startsWith("+") -> return Type.Add
            s.trim().startsWith("-") -> return Type.Delete
            else -> return Type.Neutral
        }
    }
}