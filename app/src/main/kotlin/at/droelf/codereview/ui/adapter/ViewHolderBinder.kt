package at.droelf.codereview.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ViewHolderBinder<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: T)
}