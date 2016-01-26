package at.droelf.codereview.ui.viewholder

import android.view.View
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.adapter.PullRequestFilesAdapter

class PullRequestFileViewHolder(val view: View): ViewHolderBinder<PullRequestFilesAdapter.PullRequestFileViewHolderData>(view) {

    val title: TextView
    val commentCount: TextView
    val secondLine1: TextView
    val secondLine2: TextView

    init {
        title = view.findViewById(R.id.row_pr_file_name) as TextView
        commentCount = view.findViewById(R.id.row_pr_file_comments) as TextView
        secondLine1 = view.findViewById(R.id.row_pr_file_repo) as TextView
        secondLine2 = view.findViewById(R.id.row_pr_file_user) as TextView
    }

    override fun bind(data: PullRequestFilesAdapter.PullRequestFileViewHolderData) {
        title.text = data.file.first.filename
        commentCount.text = if(data.file.second != 0) data.file.second.toString() else ""
        secondLine1.text = data.file.first.status

        if(data.file.first.changes > 0){
            view.setOnClickListener { view ->
                val file = data.file.first
                data.controller.showFile(
                        data.fm,
                        file.contentsUrl,
                        file.patch,
                        file.filename,
                        data.pr.base.repo.owner.login,
                        data.pr.base.repo.name,
                        data.pr.number
                )
            }
        }else {
            view.setOnClickListener(null)
        }
    }

}