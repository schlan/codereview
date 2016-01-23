package at.droelf.codereview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class PatchAdapter(val patchController: PatchAdapterController) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PatchAdapterControllerImpl.PatchAdapterInterface {

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
        patchController.viewHolderWrapper(position).bind(holder, patchController, position)
    }

    override fun update(range: Pair<Int, Int>) {
        notifyDataSetChanged()
    }
}
