package at.droelf.codereview.ui.adapter

import android.text.SpannableString
import android.text.SpannableStringBuilder
import at.droelf.codereview.*
import at.droelf.codereview.model.Model
import at.droelf.codereview.patch.Patch


class PatchAdapterControllerImpl(val githubDataSet: Model.GithubDataSet) : PatchAdapterController {

    val patch: Patch.Patch = githubDataSet.patch
    val rawCode: List<SpannableString> = githubDataSet.fileContent

    override var patchAdapter: PatchAdapterInterface? = null
    val patchSegmentController: List<PatchSegmentController> = patchSegmentController()

    fun patchSegmentController(): List<PatchSegmentController> {

        val commentsForSegment = githubDataSet.reviewComments.filter { it.path.equals(githubDataSet.fileName) }

        val lastSegment = patch.patchSegments.last()
        val lastLineNew = lastSegment.newRange.start + lastSegment.newRange.numLines
        val lastLineOld = lastSegment.originalRange.start + lastSegment.originalRange.numLines

        var lastSegmentController = listOf<PatchSegmentController>()
        if(lastLineNew > 0 && lastLineNew < rawCode.size){
            val numLinesToEnd = rawCode.size - lastLineNew

            val endSegment = Patch.PatchSegment(
                    Patch.Range(lastLineOld + numLinesToEnd + 1, 0),
                    Patch.Range(lastLineNew + numLinesToEnd + 1, 0),
                    listOf(),
                    "end",
                    ""
            )
            lastSegmentController = listOf(PatchSegmentController(endSegment, rawCode.subList(lastLineNew - 1, rawCode.lastIndex + 1), lastLineNew - 1, 0, commentsForSegment))
        }

        var numVisibleLines = 0

        return patch.patchSegments.mapIndexed { i, patchSegment ->
            val rawCodeRange: Pair<Int, Int> = when {
                0 == patch.patchSegments.lastIndex  && 0 == i -> {
                     Pair(0, rawCode.lastIndex + 1) // only one patch all code
                }
                0 == i -> {
                    Pair(0, patchSegment.newRange.start + patchSegment.newRange.numLines - 1)
                }
                patch.patchSegments.lastIndex == i -> {
                    val prevRange = patch.patchSegments[i - 1].newRange
                    Pair(prevRange.start + prevRange.numLines - 1, rawCode.lastIndex + 1)
                }
                else -> {
                    val prevRange = patch.patchSegments[i - 1].newRange
                    Pair(prevRange.start + prevRange.numLines - 1, patchSegment.newRange.start + patchSegment.newRange.numLines - 1)
                }
            }

            val patchSegController = PatchSegmentController(patchSegment, rawCode.subList(rawCodeRange.first, rawCodeRange.second), rawCodeRange.first, numVisibleLines, commentsForSegment)
            numVisibleLines += patchSegment.lines.size + 1
            patchSegController

        } + lastSegmentController
    }

    fun patchSegmentForPos(pos: Int): Pair<PatchSegmentController, Int> {
        val sizeList = patchSegmentController.map { it.size() }
        val sumList = sizeList.mapIndexed { i, value -> sizeList.subList(0, i+1).sum() }
        val index = sumList.indexOfFirst { it > pos }
        val posInSegment = pos - sizeList.subList(0, index).sum()
        return Pair(patchSegmentController[index], posInSegment)
    }

    override fun expand(line: Int): Unit {
        val range = patchSegmentForPos(line).first.expand(line)
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
        fun lineSelected(line: Int)
    }
}

interface PatchAdapterController {
    fun totalItemCount(): Int
    fun viewHolderWrapper(pos: Int): ViewHolderWrapper
    fun expand(line: Int): Unit
    var patchAdapter: PatchAdapterControllerImpl.PatchAdapterInterface?
}

class PatchSegmentController(
        val patchSegment: Patch.PatchSegment,
        val rawCode: List<SpannableString>,
        val offset: Int,
        val visibleOffset: Int,
        val reviewComments: List<Model.ReviewComment>
) : SpannableStringHelper {

    var viewHolderWrapper: List<ViewHolderWrapper> = initWrapper()

    fun initWrapper(): List<ViewHolderWrapper> {
        val codeLines: List<ViewHolderWrapper> = listOf(
                ViewHolderHeader(patchSegment.header, patchSegment.method, patchSegment.originalRange, patchSegment.newRange)) +
                patchSegment.lines.mapIndexed { i, l ->
                    val lineString = when(l.type){
                        Patch.Type.Delete -> l.line
                        else -> rawCode[l.modifiedNum!! - 1 - offset].prefix(if(l.type == Patch.Type.Add) "+" else " ")
                    }
                    ViewHolderLine(SpannableString(lineString), LineType.fromPatchType(l.type), l.originalNum, l.modifiedNum, i + visibleOffset) // FIXME
                }

        val comments = reviewComments.filter { comment ->
            val pos = comment.position
            pos > visibleOffset && pos < (visibleOffset + patchSegment.lines.size)
        }

        val wrapper: MutableList<ViewHolderWrapper> = arrayListOf()

        codeLines.forEachIndexed { i, viewHolderWrapper ->
            val c = comments.filter { (it.position - visibleOffset) == i }
            wrapper.add(viewHolderWrapper)
            if(c.isNotEmpty()) {
                wrapper.add(ViewHolderComment(c))
            }
        }

        return wrapper
    }

    fun size(): Int {
        return viewHolderWrapper.size
    }

    fun viewHolderWrapper(pos: Int): ViewHolderWrapper {
        return viewHolderWrapper[pos]
    }

    fun expand(pos: Int): Pair<Int, Int> {
        val header = viewHolderWrapper.first()
        if(header is ViewHolderHeader){
            val mStart = header.modifiedRange.start
            val oStart = header.originalRange.start

            val codeToAdd = if(patchSegment.newRange.start - 1 - offset > 0){
                rawCode.subList(0, patchSegment.newRange.start - 1 - offset)
            } else {
                listOf()
            }

            val newList = codeToAdd.mapIndexed { i, spannableString ->
                ViewHolderLine(spannableString.prefix(" "), LineType.Expanded, oStart - codeToAdd.size + i, mStart - codeToAdd.size + i, null)
            }
            viewHolderWrapper = newList + viewHolderWrapper.subList(1, viewHolderWrapper.size)

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