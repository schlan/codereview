package at.droelf.codereview

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import at.droelf.codereview.patch.Patch
import java.util.*

class PatchAdapter(val patch: Patch.Patch, val rawFile: List<SpannableString>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val viewHolderWrapper: List<ViewHolderWrapper>

    init {
        viewHolderWrapper = patch.patchSegments.map { seg ->
            listOf(ViewHolderWrapperHeader(seg.header, seg.method)) + seg.lines.map { l ->
                val lineString = when(l.type){
                    Patch.Type.Delete -> l.line
                    else -> {
                        SpannableStringBuilder()
                                .append(if(l.type == Patch.Type.Add) "+" else " ")
                                .append(rawFile.get(l.modifiedNum!! - 1))

                    }
                }
                ViewHolderLine(Patch.Line(SpannableString(lineString), l.type, l.originalNum, l.modifiedNum))
                //ViewHolderLine(l)
            }
        }.flatten()
    }

    override fun getItemCount(): Int {
        return viewHolderWrapper.size
    }

    override fun getItemViewType(position: Int): Int  {
        return viewHolderWrapper.get(position).viewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val type = PatchListType.fromViewType(viewType) ?: return null
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(type.layoutId, parent, false)){}

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        viewHolderWrapper.get(position).bind(holder)
    }
}
