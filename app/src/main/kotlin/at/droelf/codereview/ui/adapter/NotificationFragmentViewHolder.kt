package at.droelf.codereview.ui.adapter

import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.utils.CircleTransform
import at.droelf.codereview.utils.HumanTime
import butterknife.Bind
import butterknife.ButterKnife
import com.squareup.picasso.Picasso

class NotificationFragmentViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    @Bind(R.id.row_notification_avatar) lateinit var avatar: ImageView
    @Bind(R.id.row_notification_name) lateinit var title: TextView
    @Bind(R.id.row_notification_repo) lateinit var secondLine: TextView
    @Bind(R.id.row_notification_timestamp) lateinit var timeStamp: TextView
    @Bind(R.id.row_notification_user) lateinit var user: TextView
    @Bind(R.id.row_notification_issue_count) lateinit var issueCount: TextView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(pr: GithubModel.PullRequest, fm: FragmentManager, controller: NotificationFragmentController) {
        Picasso.with(view.context).load(pr.user.avatarUrl).transform(CircleTransform()).into(avatar)

        title.text = pr.title
        secondLine.text = pr.base.repo.fullName
        timeStamp.text = HumanTime.approximately(System.currentTimeMillis() - pr.updatedAt.time)
        user.text = "@${pr.user.login}"

        issueCount.visibility = View.GONE
        controller.lazyLoadDataForPr(pr).subscribe {
            val issueCountString = when {
                it.first > 0 && it.first < 100 -> "${it.first}"
                it.first > 99 -> "99+"
                else -> null
            }
            issueCount.visibility = if(issueCountString != null) View.VISIBLE else View.GONE
            issueCount.text = issueCountString
        }

        view.setOnClickListener {
            controller.displayFileFragment(fm, pr.base.repo.owner.login, pr.base.repo.name, pr.number)
        }
    }

}