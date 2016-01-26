package at.droelf.codereview.ui.viewholder

import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.ui.viewholder.ViewHolderBinder
import at.droelf.codereview.utils.CircleTransform
import at.droelf.codereview.utils.HumanTime
import butterknife.Bind
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class NotificationFragmentViewHolder(val view: View): ViewHolderBinder<NotificationFragmentViewHolder.NotificationFragmentViewHolderData>(view) {

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

    override fun bind(data: NotificationFragmentViewHolderData) {
        Picasso.with(view.context).load(data.pr.user.avatarUrl).transform(CircleTransform()).into(avatar)

        title.text = data.pr.title
        secondLine.text = data.pr.base.repo.fullName
        user.text = "@${data.pr.user.login}"

        val timeSpan = System.currentTimeMillis() - data.pr.updatedAt.time
        if(timeSpan < TimeUnit.DAYS.toMillis(7)){
            timeStamp.text = HumanTime.approximately(System.currentTimeMillis() - data.pr.updatedAt.time)
        } else {
            timeStamp.text = SimpleDateFormat("dd MMM yyyy").format(data.pr.updatedAt)
        }

        lazyLoadThings(data.pr, data.controller)
        view.setOnClickListener {
            data.controller.displayFileFragment(data.fm, data.pr.base.repo.owner.login, data.pr.base.repo.name, data.pr.number)
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

    data class NotificationFragmentViewHolderData(
            val pr: GithubModel.PullRequest,
            val fm: FragmentManager,
            val controller: NotificationFragmentController
    )

}