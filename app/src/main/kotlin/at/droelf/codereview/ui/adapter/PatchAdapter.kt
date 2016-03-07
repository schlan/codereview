package at.droelf.codereview.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import at.droelf.codereview.PatchListType

class PatchAdapter(val context: Context, val patchController: PatchAdapterController, val addComment: (line: Int) -> Unit, val replyComment: (commentId: Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PatchAdapterControllerImpl.PatchAdapterInterface {

    var lineNumbersVisbile: Boolean = false
        set(value) {
            field = value
            update(0 to 0)
        }

    init {
        patchController.patchAdapter = this
    }

    override fun getItemCount(): Int {
        return patchController.totalItemCount()
    }

    override fun getItemViewType(position: Int): Int  {
        return patchController.viewHolderWrapper(position).viewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val type = PatchListType.fromViewType(viewType) ?: return null
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(type.layoutId, parent, false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        patchController.viewHolderWrapper(position).bind(holder, patchController, ItemConfig(position, lineNumbersVisbile))
    }

    override fun update(range: kotlin.Pair<Int, Int>) {
        notifyDataSetChanged()
    }

    override fun lineSelected(line: Int) {
        addComment(line)
    }

    override fun commentSelected(commentId: Long) {
        replyComment(commentId)
    }

    data class ItemConfig(val pos: Int, val linesNumbersVisible: Boolean)

}
