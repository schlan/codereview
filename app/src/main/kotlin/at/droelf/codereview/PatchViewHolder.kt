package at.droelf.codereview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import at.droelf.codereview.model.Model
import at.droelf.codereview.patch.Patch
import at.droelf.codereview.ui.adapter.PatchAdapter
import at.droelf.codereview.ui.adapter.PatchAdapterController
import at.droelf.codereview.ui.view.CommentView
import at.droelf.codereview.utils.UiHelper

//////////
abstract class ViewHolderWrapper(val type: PatchListType) {
    fun viewType(): Int = type.viewType
    abstract fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, itemConfig: PatchAdapter.ItemConfig)
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

class ViewHolderHeader(val header: String, val method: String?, val originalRange: Patch.Range, val modifiedRange: Patch.Range): ViewHolderWrapper(PatchListType.Head), UiHelper {

    override fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, itemConfig: PatchAdapter.ItemConfig) {
        val text = viewholder.itemView.findViewById(R.id.row_patch_header) as TextView
        val numberContainer = viewholder.itemView.findViewById(R.id.row_patch_header_number)

        text.text = "$header $method"
        numberContainer.visibility = booleanToViewVisibilityFlag(itemConfig.linesNumbersVisible)

        viewholder.itemView.setOnClickListener {
            patchController.expand(itemConfig.pos)
        }
    }

}

class ViewHolderLine(val line: SpannableString, val lineType: LineType, val originalNum: Int?, val modifiedNum: Int?, val diffPos: Int?) : ViewHolderWrapper(PatchListType.Line), UiHelper {

    override fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, itemConfig: PatchAdapter.ItemConfig) {
        val view = viewholder.itemView
        val container = view.findViewById(R.id.row_patch_root) as RelativeLayout
        val numberContainer = view.findViewById(R.id.row_patch_number_container)
        val lineNumberOriginal = view.findViewById(R.id.row_patch_number_original) as TextView
        val lineNumberModified = view.findViewById(R.id.row_patch_number_modified) as TextView
        val codeLineTextView = view.findViewById(R.id.row_patch_text) as TextView

        codeLineTextView.text = line
        lineNumberOriginal.text = originalNum?.toString() ?: ""
        lineNumberModified.text = modifiedNum?.toString() ?: ""
        numberContainer.visibility = booleanToViewVisibilityFlag(itemConfig.linesNumbersVisible)

        when (lineType) {
            LineType.Add -> view.background = ColorDrawable(Color.parseColor("#EAFFEA"))
            LineType.Delete -> view.background = ColorDrawable(Color.parseColor("#FFECEC"))
            LineType.Expanded -> view.background = ColorDrawable(Color.parseColor("#FAFAFA"))
            else -> view.background = ColorDrawable(Color.WHITE)
        }

        container.isEnabled = diffPos != null
        container.setOnClickListener {
            if(diffPos != null) {
                val commentPos = diffPos + 1
                patchController.patchAdapter?.lineSelected(commentPos)
            }
        }
    }
}

class ViewHolderComment(val reviewComment: List<Model.ReviewComment>): ViewHolderWrapper(PatchListType.Comment){

    lateinit var container: LinearLayout

    override fun bind(viewholder: RecyclerView.ViewHolder, patchController: PatchAdapterController, itemConfig: PatchAdapter.ItemConfig) {
        container = viewholder.itemView.findViewById(R.id.row_patch_comment_container) as LinearLayout
        container.removeAllViews()
        val commentViews = reviewComment.map { CommentView(it, viewholder.itemView.context) }
        commentViews.forEach { container.addView(it) }
        commentViews.first().first()

        val id = commentViews.last().last()
        viewholder.itemView.setOnClickListener {
            patchController.patchAdapter?.commentSelected(id)
        }
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



