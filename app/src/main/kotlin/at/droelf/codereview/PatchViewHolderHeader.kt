package at.droelf.codereview

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.widget.TextView

class PatchViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {

    val header: TextView?

    init {
        header = view.findViewById(R.id.row_patch_header) as? TextView
    }

    fun bind(line: SpannableString) {
        header?.text = line
    }
}