package at.droelf.codereview

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import at.droelf.codereview.patch.Patch
import syntaxhighlight.ParseResult

class PatchAdapter(val patch: Patch.Patch, val rawFile: List<SpannableString>) : RecyclerView.Adapter<PatchViewHolder>() {

    val lines: List<Patch.Line> = patch.patchSegments.map { it.lines }.flatten()

    override fun getItemCount(): Int = rawFile.size

    override fun getItemViewType(position: Int): Int = 0//rawFile.get(position).type.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_patchadapter, parent, false)
        return PatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatchViewHolder, position: Int) {
        holder.bind(rawFile.get(position), position + 1)
    }

}
