package at.droelf.codereview.ui.adapter

import android.view.View
import android.widget.TextView
import at.droelf.codereview.R
import butterknife.Bind
import butterknife.ButterKnife

class NotificationFragmentViewHolderHeader(view: View): ViewHolderBinder<String>(view) {

    @Bind(R.id.row_notification_header_title) lateinit var titleView: TextView

    init {
        ButterKnife.bind(this, view)
    }

    override fun bind(data: String){
        titleView.text = data
    }

}