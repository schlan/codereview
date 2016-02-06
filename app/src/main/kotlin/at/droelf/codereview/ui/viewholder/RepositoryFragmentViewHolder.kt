package at.droelf.codereview.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.utils.CircleTransform
import com.squareup.picasso.Picasso
import org.honorato.multistatetogglebutton.MultiStateToggleButton

class RepositoryFragmentViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    val avatar: ImageView = view.findViewById(R.id.row_repo_avatar) as ImageView
    val repoName: TextView = view.findViewById(R.id.row_repo_name) as TextView
    val repoConfig: TextView = view.findViewById(R.id.row_notification_config) as TextView

    val prToggleButton: MultiStateToggleButton = view.findViewById(R.id.row_repo_toggle_pr) as MultiStateToggleButton
    val issueToggleButton: MultiStateToggleButton = view.findViewById(R.id.row_repo_toggle_issue) as MultiStateToggleButton

    fun bind(repo: GithubModel.Repository) {
        Picasso.with(view.context)
                .load(repo.owner.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)
        repoName.text = repo.fullName
        repoConfig.text = "PR: Mine - Issues: Part."

        prToggleButton.value = 3
        issueToggleButton.value = 3
    }

}