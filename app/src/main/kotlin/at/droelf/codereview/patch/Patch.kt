package at.droelf.codereview.patch

import android.text.SpannableString
import at.droelf.codereview.PrettyfyHighlighter
import kotlin.text.Regex


object Patch{

    private val parser: PatchParser = PatchParser()

    data class Patch(val patchSegments: List<PatchSegment>)
    data class PatchSegment(val originalRange: Range, val newRange: Range, val lines: List<Line>, val header: String, val method: String?)

    data class Line(val line: CharSequence, val type: Type, val originalNum: Int?, val modifiedNum: Int?)
    data class Range(val start: Int, val numLines: Int)

    enum class Type(val id: Int) {
        Add(1), Delete(2), Neutral(3);
    }

    fun parse(data: String): Patch? {
        return parser.parse(data)
    }
}

class PatchParser {
    val headerRegex = Regex("^@@\\ \\-\\d+\\,\\d+\\ \\+\\d+\\,\\d+\\ @@")
    val rangeRegex = Regex("^[-,+]\\d+,\\d+$")

    fun parse(data: String): Patch.Patch? {
        val lines = PrettyfyHighlighter.highlight(data, null)
        if(lines.isEmpty()) return null

        val headerIndex = lines.filter { isHeader(it) }.map { lines.indexOf(it) }
        if(headerIndex.isEmpty()) return null

        val range: List<Pair<Int, Int>> = headerIndex.subList(0, headerIndex.lastIndex).zip(headerIndex.subList(1, headerIndex.lastIndex + 1)) + Pair(headerIndex.last(), lines.lastIndex + 1)
        val patchesRaw: List<List<SpannableString>> = range.map { lines.subList(it.first, it.second) }

        return Patch.Patch(patchesRaw.map { parsePatchSegment(it) ?: return null })
    }

    fun parsePatchSegment(list: List<SpannableString>): Patch.PatchSegment? {
        val header = headerRegex.find(list.first())?.value ?: return null
        val r: Pair<Patch.Range, Patch.Range> = parseHeader(header) ?: return null
        val parsedLines: List<Patch.Line> = parseCodeLines(list.subList(1, list.lastIndex + 1), r)
        return Patch.PatchSegment(r.first, r.second, parsedLines, header, list.first().toString().replace(header, ""))
    }

    // tested
    fun parseCodeLines(list: List<CharSequence>, r: Pair<Patch.Range, Patch.Range>): List<Patch.Line> {
        var iOri = r.first.start
        var iMod = r.second.start
        return list.mapIndexed { i, s ->
            val lineType = parseLineType(s)
            val lineNumber = when (lineType) {
                Patch.Type.Neutral -> Pair(iOri++, iMod++)
                Patch.Type.Add -> Pair(null, iMod++)
                Patch.Type.Delete -> Pair(iOri++, null)
            }
            Patch.Line(s, lineType, lineNumber.first, lineNumber.second)
        }
    }

    // tested
    fun parseHeader(header: String): Pair<Patch.Range, Patch.Range>? {
        val strippedHeader: List<String> = header.replace("@", "").trim().split(" ")
        val start = parseRange(strippedHeader.first()) ?: return null
        val end = parseRange(strippedHeader.last()) ?: return null
        return Pair(start, end)
    }

    //tested
    fun parseRange(range: String): Patch.Range? {
        if(!rangeRegex.matches(range)) return null
        val r: List<String> = range.substring(1).split(",")
        if(r.size != 2) return null
        return Patch.Range(r.first().toInt(), r.last().toInt())
    }

    //tested
    fun isHeader(line: CharSequence): Boolean {
        return headerRegex.containsMatchIn(line)
    }

    //tested
    fun parseLineType(s: CharSequence): Patch.Type {
        when {
            s.startsWith("+") -> return Patch.Type.Add
            s.startsWith("-") -> return Patch.Type.Delete
            else -> return Patch.Type.Neutral
        }
    }
}