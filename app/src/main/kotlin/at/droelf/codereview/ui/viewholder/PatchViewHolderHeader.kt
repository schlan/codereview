package at.droelf.codereview.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import at.droelf.codereview.R

class PatchViewHolderHeader(val view: View) : RecyclerView.ViewHolder(view) {

    val text: TextView = view.findViewById(R.id.row_patch_header) as TextView

    fun bind(line: SpannableString) {
        text.text = line
    }
}