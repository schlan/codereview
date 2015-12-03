package at.droelf.codereview

import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife

class PatchViewHolderHeader(val view: View) : RecyclerView.ViewHolder(view) {

    @Bind(R.id.row_patch_header)
    lateinit var text: TextView

    init{
        ButterKnife.bind(view)
    }

    fun bind(line: SpannableString) {
        text.text = line
    }
}