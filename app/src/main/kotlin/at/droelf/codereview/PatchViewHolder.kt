package at.droelf.codereview

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.widget.TextView

class PatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val textView: TextView?
    val rowNumber: TextView?

    init {
        textView = view.findViewById(R.id.row_patch_text) as? TextView
        rowNumber = view.findViewById(R.id.row_patch_number) as? TextView
    }

    fun bind(line: SpannableString, pos: Int) {
        rowNumber?.text = pos.toString()
        textView?.text = line
    }

}