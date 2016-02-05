package at.droelf.codereview.ui.viewholder

import android.view.View
import android.widget.TextView
import at.droelf.codereview.R

class NotificationFragmentViewHolderHeader(view: View): ViewHolderBinder<String>(view) {

    val titleView: TextView = view.findViewById(R.id.row_notification_header_title) as TextView

    override fun bind(data: String){
        titleView.text = data
    }

}