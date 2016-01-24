package at.droelf.codereview.ui.adapter

import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.utils.CircleTransform
import at.droelf.codereview.utils.HumanTime
import butterknife.Bind
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class NotificationFragmentViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    @Bind(R.id.row_notification_avatar) lateinit var avatar: ImageView
    @Bind(R.id.row_notification_name) lateinit var title: TextView
    @Bind(R.id.row_notification_repo) lateinit var secondLine: TextView
    @Bind(R.id.row_notification_timestamp) lateinit var timeStamp: TextView
    @Bind(R.id.row_notification_user) lateinit var user: TextView
    @Bind(R.id.row_notification_issue_count) lateinit var issueCount: TextView
    @Bind(R.id.row_notification_avatar_background) lateinit var avatarBackground: RelativeLayout

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(pr: GithubModel.PullRequest, fm: FragmentManager, controller: NotificationFragmentController) {
        Picasso.with(view.context).load(pr.user.avatarUrl).transform(CircleTransform()).into(avatar)

        title.text = pr.title
        secondLine.text = pr.base.repo.fullName
        user.text = "@${pr.user.login}"


        val timeSpan = System.currentTimeMillis() - pr.updatedAt.time
        if(timeSpan < TimeUnit.DAYS.toMillis(7)){
            timeStamp.text = HumanTime.approximately(System.currentTimeMillis() - pr.updatedAt.time)
        } else {
            timeStamp.text = SimpleDateFormat("dd MMM yyyy").format(pr.updatedAt)
        }

        lazyLoadThings(pr, controller)
        view.setOnClickListener {
            controller.displayFileFragment(fm, pr.base.repo.owner.login, pr.base.repo.name, pr.number)
        }
    }

    fun lazyLoadThings(pr: GithubModel.PullRequest, controller: NotificationFragmentController){
        issueCount.visibility = View.GONE
        avatarBackground.background = null
        controller.lazyLoadDataForPr(pr).subscribe { pr ->
            val comments = pr.reviewComments + pr.comments
            val issueCountString = when {
                comments > 0 && comments < 100 -> "$comments"
                comments > 99 -> "99+"
                else -> null
            }
            issueCount.visibility = if(issueCountString != null) View.VISIBLE else View.GONE
            issueCount.text = issueCountString

            if("true".equals(pr.mergeable)) avatarBackground.setBackgroundResource(R.drawable.background_build_pass)
            if("false".equals(pr.mergeable)) avatarBackground.setBackgroundResource(R.drawable.background_build_fail)
        }
    }


}