package at.droelf.codereview.patch

import kotlin.text.Regex


object Patch{

    val headerRegex = Regex("^\\s*@@\\ \\-\\d+\\,\\d+\\ \\+\\d+\\,\\d+\\ @@")

    data class Patch(val patchSegments: List<PatchSegment>)
    data class PatchSegment(val originalRange: Range, val newRange: Range, val lines: List<Line>, val header: String, val method: String?)

    data class Line(val line: String, val type: Type)
    data class Range(val start: Int, val numLines: Int)

    enum class Type(val id: Int) {
        Add(1), Delete(2), Neutral(3);
    }

    fun parse(data: String): Patch? {
        val lines: List<String> = data.split("\n")
        if(lines.isEmpty()) return null

        val headerIndex = lines.filter { headerRegex.containsMatchIn(it.trim()) }.map { lines.indexOf(it) }
        if(headerIndex.isEmpty()) return null

        val range: List<Pair<Int, Int>> = headerIndex.subList(0, headerIndex.lastIndex - 1).zip(headerIndex.subList(1, headerIndex.lastIndex))
        val patchesRaw: List<List<String>> = range.map { lines.subList(it.first, it.second) }
        return Patch( patchesRaw.map { parsePatchSegment(it)} )
    }

    private fun parsePatchSegment(list: List<String>): PatchSegment {
        val header = headerRegex.find(list.first())
        val r: Pair<Range, Range> = parseHeader(header!!.value) //FIXME
        val parsedLines: List<Line> = list.subList(1, list.lastIndex).map { Line(it, parseLineType(it)) }
        return PatchSegment(r.first, r.second, parsedLines, header.value, list.first().replace(header.value, ""))
    }


    private fun parseHeader(header: String): Pair<Range, Range> {
        val strippedHeader: List<String> = header.replace("@", "").trim().split(" ")
        return Pair(parseRange(strippedHeader.first()), parseRange(strippedHeader.last()))
    }

    private fun parseRange(range: String): Range {
        val r: List<String> = range.substring(1).split(",")
        return Range(r.first().toInt(), r.last().toInt())
    }

    private fun parseLineType(s: String): Type {
        when {
            s.trim().startsWith("+") -> return Type.Add
            s.trim().startsWith("-") -> return Type.Delete
            else -> return Type.Neutral
        }
    }
}