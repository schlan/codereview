package at.droelf.codereview.ui.viewholder

import android.view.View
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.ui.viewholder.ViewHolderBinder
import butterknife.Bind
import butterknife.ButterKnife

class NotificationFragmentViewHolderHeader(view: View): ViewHolderBinder<String>(view) {

    val titleView: TextView = view.findViewById(R.id.row_notification_header_title) as TextView

    override fun bind(data: String){
        titleView.text = data
    }

}