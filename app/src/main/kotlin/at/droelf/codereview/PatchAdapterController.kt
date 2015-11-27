package at.droelf.codereview

import android.text.SpannableString
import android.text.SpannableStringBuilder
import at.droelf.codereview.patch.Patch


class PatchAdapterControllerImpl(val patch: Patch.Patch, val rawCode: List<SpannableString>) : PatchAdapterController {

    override var patchAdapter: PatchAdapterInterface? = null
    val patchSegmentController: List<PatchSegmentController> = patchSegmentController()

    fun patchSegmentController(): List<PatchSegmentController> {
        return patch.patchSegments.mapIndexed { i, patchSegment ->
            val rawCodeRange: Pair<Int, Int> = when {
                0 == patch.patchSegments.lastIndex  && 0 == i -> {
                     Pair(0, rawCode.lastIndex + 1) // only one patch all code
                }
                0 == i -> {
                    Pair(0, patchSegment.newRange.start + patchSegment.newRange.numLines - 1)
                }
                patch.patchSegments.lastIndex == i -> {
                    val prevRange = patch.patchSegments.get(i - 1).newRange
                    Pair(prevRange.start + prevRange.numLines - 1, rawCode.lastIndex + 1)
                }
                else -> {
                    val prevRange = patch.patchSegments.get(i - 1).newRange
                    Pair(prevRange.start + prevRange.numLines - 1, patchSegment.newRange.start + patchSegment.newRange.numLines - 1)
                }
            }
            PatchSegmentController(patchSegment, rawCode.subList(rawCodeRange.first, rawCodeRange.second), rawCodeRange.first)
        }
    }

    fun patchSegmentForPos(pos: Int): Pair<PatchSegmentController, Int> {
        val sizeList = patchSegmentController.map { it.size() }
        val sumList = sizeList.mapIndexed { i, value -> sizeList.subList(0, i+1).sum() }
        val index = sumList.indexOfFirst { it > pos }
        val posInSegment = pos - sizeList.subList(0, index).sum()
        return Pair(patchSegmentController.get(index), posInSegment)
    }

    override fun expand(pos: Int): Unit {
        val range = patchSegmentForPos(pos).first.expand(pos)
        patchAdapter?.update(range)
    }

    override fun viewHolderWrapper(pos: Int): ViewHolderWrapper {
        val posRep = patchSegmentForPos(pos)
        return posRep.first.viewHolderWrapper(posRep.second)
    }

    override fun totalItemCount(): Int {
        return patchSegmentController.sumBy { it.size() }
    }

    interface PatchAdapterInterface {
        fun update(range: Pair<Int, Int>)
    }
}

interface PatchAdapterController {
    fun totalItemCount(): Int
    fun viewHolderWrapper(pos: Int): ViewHolderWrapper
    fun expand(line: Int): Unit
    var patchAdapter: PatchAdapterControllerImpl.PatchAdapterInterface?
}


class PatchSegmentController(val patchSegment: Patch.PatchSegment, val rawCode: List<SpannableString>, val offset: Int) : SpannableStringHelper {

    var viewHolderWrapper: List<ViewHolderWrapper> = initWrapper()

    fun initWrapper(): List<ViewHolderWrapper> {
        return listOf(ViewHolderHeader(patchSegment.header, patchSegment.method, patchSegment.originalRange, patchSegment.newRange)) + patchSegment.lines.map { l ->
                val lineString = when(l.type){
                    Patch.Type.Delete -> l.line
                    else -> {
                        try {
                            rawCode.get(l.modifiedNum!! - 1 - offset).prefix(if(l.type == Patch.Type.Add) "+" else " ")
                        } catch(e: Exception){
                            println()
                            SpannableString("moep")
                        }
                    }
                }
                ViewHolderLine(SpannableString(lineString), ViewHolderLine.LineType.fromPatchType(l.type), l.originalNum, l.modifiedNum)
            }
    }


    fun size(): Int {
        return viewHolderWrapper.size
    }

    fun viewHolderWrapper(pos: Int): ViewHolderWrapper {
        return viewHolderWrapper.get(pos)
    }

    fun expand(pos: Int): Pair<Int, Int> {
        val header = viewHolderWrapper.first()
        if(header is ViewHolderHeader){
            val mStart = header.modifiedRange.start
            val oStart = header.originalRange.start

            val codeToAdd = rawCode.subList(0, patchSegment.newRange.start - 1 - offset)
            val newList = codeToAdd.mapIndexed { i, spannableString ->
                ViewHolderLine(spannableString.prefix(" "), ViewHolderLine.LineType.Expanded, oStart - codeToAdd.size + i, mStart - codeToAdd.size + i)
            }
            viewHolderWrapper = newList + viewHolderWrapper.subList(1, viewHolderWrapper.lastIndex + 1)

            return Pair(pos, pos + codeToAdd.size - 1) // FIXME
        }

        return Pair(pos, pos)
    }

}

interface SpannableStringHelper {
    fun SpannableString.prefix(prefix: String): SpannableString {
        return SpannableString(SpannableStringBuilder().append(prefix).append(this))
    }
}