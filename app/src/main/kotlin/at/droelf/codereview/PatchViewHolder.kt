package at.droelf.codereview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import at.droelf.codereview.model.Model
import at.droelf.codereview.patch.Patch
import at.droelf.codereview.ui.adapter.PatchAdapterController
import at.droelf.codereview.ui.view.CommentView

//////////
abstract class ViewHolderWrapper(val type: PatchListType) {
    fun viewType(): Int = type.viewType
    abstract fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, pos: Int)
}

enum class PatchListType(val layoutId: Int, val viewType: Int, val holder: (view: View) -> RecyclerView.ViewHolder){
    Head(R.layout.row_patchadapter_header, 1, { viewholder(it) }),
    Line(R.layout.row_patchadapter, 2, { viewholder(it) }),
    Comment(R.layout.row_patchadapter_comment, 3, { viewholder(it) })
    ;

    companion object{
        fun fromViewType(viewType: Int): PatchListType? {
            return values().find { it.viewType == viewType }
        }
        private fun viewholder(view: View) = object : RecyclerView.ViewHolder(view){}
    }
}
/////////

class ViewHolderHeader(val header: String, val method: String?, val originalRange: Patch.Range, val modifiedRange: Patch.Range): ViewHolderWrapper(PatchListType.Head) {

    override fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, pos: Int) {
        val text = viewholder.itemView.findViewById(R.id.row_patch_header) as TextView
        text.text = "$header $method"

        viewholder.itemView.setOnClickListener {
            patchController.expand(pos)
        }
    }

}

class ViewHolderLine(val line: SpannableString, val lineType: LineType, val originalNum: Int?, val modifiedNum: Int?): ViewHolderWrapper(PatchListType.Line){

    override fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, pos: Int) {
        val view = viewholder.itemView
        val lineNumberOriginal = view.findViewById(R.id.row_patch_number_original) as TextView
        val lineNumberModified = view.findViewById(R.id.row_patch_number_modified) as TextView
        val codeLineTextView = view.findViewById(R.id.row_patch_text) as TextView

        codeLineTextView.text = line
        lineNumberOriginal.text = originalNum?.toString() ?: ""
        lineNumberModified.text = modifiedNum?.toString() ?: ""

        when(lineType){
            LineType.Add -> view.background = ColorDrawable(Color.parseColor("#EAFFEA"))
            LineType.Delete -> view.background = ColorDrawable(Color.parseColor("#FFECEC"))
            LineType.Expanded -> view.background = ColorDrawable(Color.parseColor("#FAFAFA"))
            else -> view.background = ColorDrawable(Color.WHITE)
        }
    }

    class ViewHolderComment(val reviewComment: List<Model.ReviewComment>): ViewHolderWrapper(PatchListType.Comment){

        lateinit var container: LinearLayout

        override fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, pos: Int) {
            container = viewholder.itemView.findViewById(R.id.row_patch_comment_container) as LinearLayout
            container.removeAllViews()
            val commentViews = reviewComment.map { CommentView(it, viewholder.itemView.context) }
            commentViews.first().first()
            commentViews.last().last()
            commentViews.forEach { container.addView(it) }
        }

    }

    enum class LineType{
        Add, Delete, Unmodified, Expanded, Comment;

        companion object {
            fun fromPatchType(patchType: Patch.Type): LineType {
                return when(patchType){
                    Patch.Type.Add -> Add
                    Patch.Type.Delete -> Delete
                    else -> Unmodified
                }
            }
        }

    }
}


