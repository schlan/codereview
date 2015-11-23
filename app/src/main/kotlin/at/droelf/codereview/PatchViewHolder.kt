package at.droelf.codereview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import at.droelf.codereview.patch.Patch

//////////
abstract class ViewHolderWrapper(val type: PatchListType) {
    fun viewType(): Int = type.viewType
    abstract fun bind(viewholder: RecyclerView.ViewHolder)
}
/////////

class ViewHolderWrapperHeader(val header: String, val method: String?): ViewHolderWrapper(PatchListType.Head) {
    override fun bind(viewholder: RecyclerView.ViewHolder) {
        val text = viewholder.itemView.findViewById(R.id.row_patch_header) as TextView
        text.text = header
    }
}

class ViewHolderLine(val patchLine: Patch.Line): ViewHolderWrapper(PatchListType.Line){
    override fun bind(viewholder: RecyclerView.ViewHolder) {
        val view = viewholder.itemView
        val lineNumberOriginal = view.findViewById(R.id.row_patch_number_original) as TextView
        val lineNumberModified = view.findViewById(R.id.row_patch_number_modified) as TextView
        val line = view.findViewById(R.id.row_patch_text) as TextView

        line.text = patchLine.line
        lineNumberOriginal.text = patchLine.originalNum?.toString() ?: ""
        lineNumberModified.text = patchLine.modifiedNum?.toString() ?: ""

        when(patchLine.type){
            Patch.Type.Add -> view.background = ColorDrawable(Color.parseColor("#EAFFEA"))
            Patch.Type.Delete -> view.background = ColorDrawable(Color.parseColor("#FFECEC"))
            else -> view.background = ColorDrawable(Color.WHITE)
        }
    }
}

enum class PatchListType(val layoutId: Int, val viewType: Int, val holder: (view: View) -> RecyclerView.ViewHolder){
    Head(R.layout.row_patchadapter_header, 1, { viewholder(it) }),
    Line(R.layout.row_patchadapter, 2, { viewholder(it) });

    companion object{
        fun fromViewType(viewType: Int): PatchListType? {
            return PatchListType.values.find { it.viewType == viewType }
        }
        private fun viewholder(view: View) = object : RecyclerView.ViewHolder(view){}
    }
}

